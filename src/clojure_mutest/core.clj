(ns clojure-mutest.core
  (:require [rewrite-clj.zip :as z]))

(defn test0
  [text]
  (let [
        zloc (z/of-string text)
        sexpr (z/sexpr zloc)
  ] (prn sexpr)))
