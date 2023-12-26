(ns clojure-mutest.mutators
  (:require [clojure-mutest.logger :as log]
            [clojure.string :as s]
            [rewrite-clj.zip :as z]))

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
        (let [cond-zloc (z/right list-zloc)
              then-node (-> cond-zloc z/right z/node)]
          [(z/replace node then-node)])))))


(def ^:private all-mutations {"and-or" and-or
                              "gt-gte" gt-gte
                              "lt-lte" lt-lte
                              "true-false" true-false
                              "plus-mul" plus-mul
                              "swap-zero" swap-zero
                              "eq-noteq" eq-noteq
                              "empty?-seq" empty?-seq
                              "not-boolean" not-boolean
                              "replace-if-with-then" replace-if-with-then})

(defn ^:private mutations-impl [config]
  (let [mutators-filter (:mutators config)
        mutators (if (= mutators-filter "all")
                   (vals all-mutations)
                   (-> all-mutations
                       (select-keys mutators-filter)
                       vals))
        mutators-str (if (= mutators-filter "all") "all" (s/join ", " mutators-filter))
        _ (log/log-info "Running " mutators-str " mutators")]
    mutators))

(def ^:private mutations (memoize mutations-impl))

(defn mutate [zipper config]
  (->> (mutations config)
       (mapcat (fn [m]
                 (try
                   (m zipper)
                   (catch UnsupportedOperationException ex
                     nil))))
       (remove nil?)))