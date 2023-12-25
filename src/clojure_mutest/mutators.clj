(ns clojure-mutest.mutators
  (:require [rewrite-clj.zip :as z]))

(defn- swapping-mutation [from to]
  (fn [node]
    (condp = (z/sexpr node)
      to [(z/replace node from)]
      from [(z/replace node to)]
      nil)))

(def ^:private and-or
  (swapping-mutation 'and 'or))

(def ^:private plus-mul
  (swapping-mutation '+ '*))

(def ^:private true-false
  (swapping-mutation 'true 'false))

(def ^:private not-boolean
  (swapping-mutation 'boolean 'not))

(def ^:private empty?-seq
  (swapping-mutation 'empty? 'seq))

(def ^:private gt-gte
  (swapping-mutation '<= '<))

(def ^:private lt-lte
  (swapping-mutation '>= '>))

(def ^:private eq-noteq
  (swapping-mutation '= 'not=))

(defn- swap-zero [node]
  (if (int? (z/sexpr node))
    (do
    (if (= 0 (z/sexpr node))
      [(z/replace node 7)]
        [(z/replace node 0)]))
    nil))

(def ^:private mutations
  [and-or
   gt-gte
   lt-lte
   true-false
   plus-mul
   swap-zero
   eq-noteq
   empty?-seq
   not-boolean])

(defn mutate [zipper]
  (->> mutations
       (mapcat (fn [m]
                 (try
                   (m zipper)
                   (catch UnsupportedOperationException ex
                     nil))))
       (remove nil?)))