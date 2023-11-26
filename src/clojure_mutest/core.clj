(ns clojure-mutest.core
  (:require [rewrite-clj.zip :as z]))

(defn z-number-mutator [zloc]
  (-> zloc
      (z/postwalk (fn select [zloc] (int? (z/sexpr zloc)))
                  (fn visit [zloc] (z/edit zloc + 100)))))

(defn test0
  [text]
  (-> (z/of-string text)
      (z-number-mutator)
      (z/root-string)))

(defn mutate-file [input-filename output-filename]
  (let [d (->
           (z/of-file* input-filename)
           z-number-mutator
           z/root-string)] (spit output-filename d)))

(defn -main []
  (mutate-file "resources/lab2.clj" "output/lab2.clj"))