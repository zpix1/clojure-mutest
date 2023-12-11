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
          "(and true true)"))

    (is
     (in? (->> "(+ 1 2)"
               (#(z/of-string* % {:track-position? true}))
               util/get-mutants
               (map #(:mutant %))
               (map z/string))
          "(* 1 2)"))

    (is
     (in? (->> "(= 3 3)"
               (#(z/of-string* % {:track-position? true}))
               util/get-mutants
               (map #(:mutant %))
               (map z/string))
          "(not= 3 3)"))

    (is
     (in? (->> "(empty? [1 2 3])"
               (#(z/of-string* % {:track-position? true}))
               util/get-mutants
               (map #(:mutant %))
               (map z/string))
          "(seq [1 2 3])"))

    (is
     (in? (->> "(> 5 3)"
               (#(z/of-string* % {:track-position? true}))
               util/get-mutants
               (map #(:mutant %))
               (map z/string))
          "(>= 5 3)"))

    (is
     (in? (->> "(<= 7 7)"
               (#(z/of-string* % {:track-position? true}))
               util/get-mutants
               (map #(:mutant %))
               (map z/string))
          "(< 7 7)")))

  (testing "swap-zero test"
    (is
     (in? (->> "(* 0 2)"
               (#(z/of-string* % {:track-position? true}))
               util/get-mutants
               (map #(:mutant %))
               (map z/string))
          "(* 7 2)"))))