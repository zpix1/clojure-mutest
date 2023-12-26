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
     (in? (as-> "(and true false)" expr
            (#(z/of-string* % {:track-position? true}) expr)
            (util/get-mutants expr "all")
            (map #(:mutant %) expr)
            (map z/string expr))
          "(or true false)"
          "(and false false)"
          "(and true true)"))

    (is
     (in? (as-> "(+ 1 2)" expr
            (#(z/of-string* % {:track-position? true}) expr)
            (util/get-mutants expr "all")
            (map #(:mutant %) expr)
            (map z/string expr))
          "(* 1 2)"))

    (is
     (in? (as-> "(= 3 3)" expr
            (#(z/of-string* % {:track-position? true}) expr)
            (util/get-mutants expr "all")
            (map #(:mutant %) expr)
            (map z/string expr))
          "(not= 3 3)"))

    (is
     (in? (as-> "(empty? [1 2 3])" expr
            (#(z/of-string* % {:track-position? true}) expr)
            (util/get-mutants expr "all")
            (map #(:mutant %) expr)
            (map z/string expr))
          "(seq [1 2 3])"))

    (is
     (in? (as-> "(> 5 3)" expr
            (#(z/of-string* % {:track-position? true}) expr)
            (util/get-mutants expr "all")
            (map #(:mutant %) expr)
            (map z/string expr))
          "(>= 5 3)"))

    (is
     (in? (as-> "(<= 7 7)" expr
            (#(z/of-string* % {:track-position? true}) expr)
            (util/get-mutants expr "all")
            (map #(:mutant %) expr)
            (map z/string expr))
          "(< 7 7)")))

  (testing "swap-zero test"
    (is
     (in? (as-> "(* 0 2)" expr
            (#(z/of-string* % {:track-position? true}) expr)
            (util/get-mutants expr "all")
            (map #(:mutant %) expr)
            (map z/string expr))
          "(* 7 2)"))))