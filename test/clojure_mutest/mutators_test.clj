(ns clojure-mutest.mutators-test
  (:require
   [clojure.test :refer :all]
   [rewrite-clj.zip :as z]
   [clojure-mutest.util :as util]))

(defn in?
  "true if coll contains all elements of elms"
  [coll & elms]
  (every? #(contains? (set coll) %) elms))

(deftest mutators-test
  (testing "swap tests"
    (is
     (in? (->> "(and true false)"
               (#(z/of-string* % {:track-position? true}))
               util/get-mutants
               (map #(:mutant %))
               (map z/string))
          "(or true false)"
          "(and false false)"
          "(and true true)"))))