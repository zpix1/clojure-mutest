(ns clojure-mutest.util
  (:require [clojure-mutest.hash-node :refer [string-hash]]
            [clojure-mutest.html-formatter :as html]
            [clojure-mutest.logger :as log]
            [clojure-mutest.mutators :as m]
            [clojure.string :as str]
            [rewrite-clj.zip :as z]))



(defn- reverse-path [path] (map {z/down z/up, z/right z/left} (reverse path)))

(defn- nth-if [coll index] (if
                            (and (< index (count coll))
                                 (> index 0)) (nth coll index)
                            nil))

(defn- extract-lines [lines index]
  (->> [(nth-if lines (dec index))
        (nth-if lines index)
        (nth-if lines (inc index))]
       (remove nil?)
       (remove empty?)
       (str/join "\n")))

(defn- get-line-str [node root-str]
  (let [pos (z/position node)
        line-no (- (first pos) 1)
        lines (str/split-lines root-str)
        line (extract-lines lines line-no)] line))

(defn- mutants [zipper paths mutators-to-run]
  (->> (for [path paths]
         (let [node (reduce (fn [node dir] (dir node)) zipper path)
               node-pos (z/position node)
               rev-path (reverse-path path)]
           (->> (remove #(nil? (:mutant-node %))
                        (for [mutant-node (m/mutate node mutators-to-run)]
                          {:mutant (reduce
                                    (fn [node dir] (dir node))
                                    mutant-node
                                    rev-path)
                           :mutant-node mutant-node}))
                (map (fn [mutant] {:mutant (:mutant mutant)
                                   :hash (:mutant-hash mutant)
                                   :node-before node
                                   :node-after (:mutant-node mutant)
                                   :pos node-pos})))))
       (apply concat)))

(defn- all-paths [zipper]
  (let [directions [z/down z/right]
        rec (fn rec [prefix node]
              (mapcat
               (fn [dir]
                 (if-let [sub-node (dir node)]
                   (cons (conj prefix dir)
                         (rec (conj prefix dir) sub-node)) nil))
               directions))]
    (cons [] (rec [z/down] (z/down zipper)))))

(defn use-output [path output]
  (print (str "[REPORT ] Clojure Mutest report:\n"
              "[REPORT ] Mutants killed:   " (:killed output) "\n"
              "[REPORT ] Mutants survived: " (- (:total output) (:killed output)) "\n"
              "[REPORT ] Total mutants:    " (:total output) "\n"))
  (if (not= (:total output) (:killed output))
    (log/log-warning "Not all mutants were killed, your tests must be updated, refer HTML report.")
    (log/log-info "All mutants were killed, good job!"))
  (spit path (html/create-html output)))

(defn get-mutants [zloc mutators-to-run]
  (-> zloc
      (mutants (all-paths zloc) mutators-to-run)))

(defn file-mutest [filename run-tests config]
  (let [file-form (z/of-file* filename {:track-position? true})
        mutated-forms (get-mutants file-form (:mutators config))
        original-root-str (z/string file-form)
        _ (log/log-info "Found " (count mutated-forms) "different mutants")
        out (try
              ;; TODO: How to avoid doall?
              (doall
               (for [mutant-data mutated-forms]
                 (let [mutated-root-str (z/string (:mutant mutant-data))
                       _ (log/log-info "Running tests for mutant"
                                       (get-line-str (:node-before mutant-data) mutated-root-str))
                       _ (spit filename mutated-root-str)
                       result (run-tests)
                       _ (log/log-info "Test result" result)
                       test-data {:killed (not result)
                                  :filename filename
                                  :hash (string-hash mutated-root-str)
                                  :line (first (:pos mutant-data))
                                  :column (second (:pos mutant-data))
                                  :before (get-line-str (:node-before mutant-data) original-root-str)
                                  :after (get-line-str (:node-after mutant-data) mutated-root-str)}]
                   test-data)))
              (catch Exception e
                (log/log-warning "Exception while running test" e))
              (finally (log/log-info "Testing done, reverting ")
                       (spit filename original-root-str)))]
    out))