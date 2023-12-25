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
    (if (= 0 (z/sexpr node))
      [(z/replace node 7)]
      [(z/replace node 0)])
    nil))

(defn replace-if-with-then [node]
  (when (= :list (z/tag node))
    (let [list-zloc (z/down node)]
      (when (= 'if (z/sexpr list-zloc))
        (let [cond-zloc (z/right list-zloc)]
          (when (boolean? (z/sexpr cond-zloc))
            (let [then-node (-> cond-zloc z/right z/node)]
              [(z/replace node then-node)])))))))

(def ^:private mutations
  [and-or
   gt-gte
   lt-lte
   true-false
   plus-mul
   swap-zero
   eq-noteq
   empty?-seq
   not-boolean
   replace-if-with-then])

(defn mutate [zipper]
  (->> mutations
       (mapcat (fn [m]
                 (try
                   (m zipper)
                   (catch UnsupportedOperationException ex
                     nil))))
       (remove nil?)))