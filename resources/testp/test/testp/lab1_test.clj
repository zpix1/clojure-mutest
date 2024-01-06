(ns testp.lab1-test
  (:require [clojure.test :refer :all]
            [testp.lab1 :as lab1]))

(deftest lab1-test
  (testing "lab12 abc n=2 works"
    (is (= (lab1/words-without-repeats "abc" 2)
           '("ab" "ac" "ba" "bc" "ca" "cb"))
        0))
  (testing "filter' works"
    (is (= (lab1/filter' #(> %1 3) '(1 2 3 4 5 6 5 3 2 1))
           (vec '(4 5 6 5)))
        0)))
