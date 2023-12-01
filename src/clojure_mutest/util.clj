(ns clojure-mutest.util
  (:require [clojure-mutest.mutators :as m]
            [rewrite-clj.zip :as z]))

(defn- reverse-path [path] (map {z/down z/up, z/right z/left} (reverse path)))

(defn- mutants [zipper paths]
  (->> (for [path paths]
         (let [node (reduce (fn [node dir] (dir node)) zipper path)
               rev-path (reverse-path path)]
           (remove nil?
                   (for [mutant (m/mutate node)]
                     (reduce
                      (fn [node dir] (dir node))
                      mutant
                      rev-path)))))
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

(defn format-output [output]
  (println output))

(defn file-mutest [filename run-tests params]
  (let [file-form (z/of-file* filename)
        paths (all-paths file-form)
        mutated-forms (mutants file-form paths)]
    (println "Found " (count mutated-forms) "different mutants")
    (try
      (format-output
       (reduce (fn [acc elem] {:total (+ (:total acc) 1)
                               :killed (+ (:killed acc) (if (:killed elem) 0 1))})
               {:total 0
                :killed 0}
               (for [mutant mutated-forms]
                 (let [mutant-string (z/string mutant)
                       _ (prn "Running tests for mutant" mutant-string)
                       _ (spit filename mutant-string)
                       result (run-tests)
                       _ (println "Test result" result)]
                   (if result
                     {:killed false
                      :filename filename
                      :mutant mutant-string}
                     {:killed true
                      :filename filename
                      :mutant mutant-string}))))))

    (println "Testing done, reverting ")
    (spit filename (z/string file-form)))
  (shutdown-agents))