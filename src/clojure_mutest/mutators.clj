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
      (z/replace node 0)
      (z/replace node 7))
    nil))
;; (defn- for->doseq [node]
;;   (if (= 'for (z/sexpr node))
;;     [(z/replace node 'doseq)]))

;; (defn- random-keyword [node]
;;   (let [sexpr (z/sexpr node)]
;;     (if (keyword? sexpr)
;;       (case sexpr
;;         :foo [(z/replace node :bar)]
;;         [(z/replace node :foo)]))))

;; (defn- rm-args [node]
;;   (let [sexpr (z/sexpr node)]
;;     (if (seq? sexpr)
;;       (let [[defn name args & more] sexpr]
;;         (if (and (#{'defn 'defn-} defn)
;;                  (vector? args))
;;           (for [arg args]
;;             (-> node z/down z/right z/right
;;                 (z/edit (partial filterv (complement #{arg})))
;;                 (z/up))))))))

;; (defn- rm-fn-body [node]
;;   (let [sexpr (z/sexpr node)]
;;     (if (seq? sexpr)
;;       (let [[defn name args & more] sexpr]
;;         (if (and (#{'defn 'defn-} defn)
;;                  (vector? args))
;;           (for [idx (drop 3 (range (count sexpr)))]
;;             (-> (iterate z/right (z/down node))
;;                 (nth idx)
;;                 z/remove
;;                 z/up
;;                 z/up)))))))

(def ^:private mutations
  [and-or
   gt-gte
   lt-lte
   true-false
   plus-mul
  ;;  swap-zero
  ;;  rm-args
  ;;  rm-fn-body
   eq-noteq
   empty?-seq
  ;;  for->doseq
  ;;  random-keyword
   not-boolean])

(defn mutate [zipper]
  (->> mutations
       (mapcat (fn [m]
                 (try
                   (m zipper)
                   (catch UnsupportedOperationException ex
                     nil))))
       (remove nil?)))