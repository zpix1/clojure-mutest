(ns clojure-mutest.util
  (:require [clojure-mutest.hash-node :refer [string-hash]]
            [clojure-mutest.html-formatter :as html]
            [clojure-mutest.logger :as log]
            [clojure-mutest.mutators :as m]
            [clojure.java.io :refer [make-parents]]
            [clojure.java.shell :as sh]
            [clojure.string :as str]
            [rewrite-clj.zip :as z]
            [clojure.java.io :as io]))



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

(defn print-results [html-path mutest-results]
  (print (str "[REPORT ] Clojure Mutest report:\n"
              "[REPORT ] Mutants killed:   " (:killed mutest-results) "\n"
              "[REPORT ] Mutants survived: " (- (:total mutest-results) (:killed mutest-results)) "\n"
              "[REPORT ] Total mutants:    " (:total mutest-results) "\n"))
  (if (not= (:total mutest-results) (:killed mutest-results))
    (log/log-warning "Not all mutants were killed, your tests must be updated, refer HTML report.")
    (log/log-info "All mutants were killed, good job!"))
  (spit html-path (html/create-html mutest-results)))

(defn mkdir [dir]
  (->> dir
       str
       java.io.File.
       .mkdir))

(defn get-parent-dir [file]
  (let [io_file (io/file file)]
    (-> io_file
        .getParent)))

(defn get-filename [file]
  (let [io_file (io/file file)]
    (-> io_file
        .getName)))

(defn get-git-diff [filename]
  (let
   [diff (:out (sh/sh "git" "-C" (get-parent-dir filename) "diff" (get-filename filename)))]
    diff))

(defn save-git-diff [diffs-dir orig-filename mutated-hash]
  (mkdir diffs-dir)
  (let [mutant-diff (get-git-diff orig-filename)
        diff-filename (-> diffs-dir
                          (io/file (str mutated-hash ".diff"))
                          (.getPath))]
    (log/log-info "Saving git diff of " orig-filename " to " diff-filename)
    (spit diff-filename mutant-diff)
    diff-filename))

(defn get-mutants [zloc mutators-to-run]
  (-> zloc
      (mutants (all-paths zloc) mutators-to-run)))

(defn file-mutest [filename run-tests config]
  (let [file-form (z/of-file* filename {:track-position? true})
        mutated-forms (get-mutants file-form (:mutators config))
        original-root-str (z/string file-form)
        diffs-dir (:git-diff-output-dir config)
        _ (log/log-info "Found " (count mutated-forms) "different mutants")
        out (try
              ;; TODO: How to avoid doall?
              (doall
               (for [mutant-data mutated-forms]
                 (let [mutated-root-str (z/string (:mutant mutant-data))
                       _ (log/log-info "Running tests for mutant"
                                       (get-line-str (:node-before mutant-data) mutated-root-str))
                       _ (spit filename mutated-root-str)
                       mutated_hash (string-hash mutated-root-str)
                       result (run-tests)
                       git-diff-path (when result (save-git-diff diffs-dir filename mutated_hash))
                       _ (log/log-info "Test result" result)
                       test-data {:killed (not result)
                                  :filename filename
                                  :hash mutated_hash
                                  :line (first (:pos mutant-data))
                                  :column (second (:pos mutant-data))
                                  :before (get-line-str (:node-before mutant-data) original-root-str)
                                  :after (get-line-str (:node-after mutant-data) mutated-root-str)
                                  :git-diff-path git-diff-path}]
                   test-data)))
              (catch Exception e
                (log/log-warning "Exception while running test" e))
              (finally (log/log-info "Testing done, reverting ")
                       (spit filename original-root-str)))]
    out))