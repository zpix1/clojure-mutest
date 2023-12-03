(ns clojure-mutest.util
  (:require [clojure-mutest.mutators :as m]
            [clojure-mutest.html-formatter :as html]
            [rewrite-clj.zip :as z]
            [clojure-mutest.logger :as log]))

(defn- reverse-path [path] (map {z/down z/up, z/right z/left} (reverse path)))

(defn- get-line-str [node] (loop [line-node node
                                  prev node]
                             (if (not= (first (z/position line-node))
                                       (first (z/position node)))
                               (z/string prev)
                               (recur (z/up line-node) line-node))))

(defn- mutants [zipper paths]
  (->> (for [path paths]
         (let [node (reduce (fn [node dir] (dir node)) zipper path)
               node-pos (z/position node)
               rev-path (reverse-path path)]
           (->> (remove #(nil? (:mutant-node %))
                        (for [mutant-node (m/mutate node)]
                          {:mutant (reduce
                                    (fn [node dir] (dir node))
                                    mutant-node
                                    rev-path)
                           :mutant-node mutant-node}))
                (map (fn [mutant] {:mutant (:mutant mutant)
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

;; (defn mutate-file [input-filename output-filename]
;;   (let [form (z/of-file input-filename)
;;         paths (all-paths form)
;;         muts (mutants form paths)
;;         d (z/root-string (first muts))]
;;     (spit output-filename d)))

(defn use-output [output]
  (print (str "[REPORT ] Clojure Mutest report:\n"
              "[REPORT ] Mutants killed:   " (:killed output) "\n"
              "[REPORT ] Mutants survived: " (- (:total output) (:killed output)) "\n"
              "[REPORT ] Total mutants:    " (:total output) "\n"))
  (if (not= (:total output) (:killed output))
    (log/log-warning "Not all mutants were killed, your tests must be updated, refer HTML report.")
    (log/log-info "All mutants were killed, good job!"))
  (spit "output.html" (html/create-html output)))

(defn file-mutest [filename run-tests config]
  (let [file-form (z/of-file* filename {:track-position? true})
        paths (all-paths file-form)
        mutated-forms (mutants file-form paths)
        _ (log/log-info "Found " (count mutated-forms) "different mutants")
        out (try
              ;; TODO: How to avoid doall?
              (doall
               (for [mutant-data mutated-forms]
                 (let [mutant-string (z/string (:mutant mutant-data))
                       _ (log/log-info "Running tests for mutant" (get-line-str (:node-before mutant-data)))
                       _ (spit filename mutant-string)
                       result (run-tests)
                       _ (log/log-info "Test result" result)
                       test-data {:killed (not result)
                                  :filename filename
                                  :line (first (:pos mutant-data))
                                  :column (second (:pos mutant-data))
                                  :before (get-line-str (:node-before mutant-data))
                                  :after (get-line-str (:node-after mutant-data))}]
                   test-data)))
              (catch Exception e
                (log/log-warning "Exception while running test" e))
              (finally (log/log-info "Testing done, reverting ")
                       (spit filename (z/string file-form))))]
    out))