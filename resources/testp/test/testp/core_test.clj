(ns testp.core-test
  (:require [clojure.test :refer :all]
            [testp.core :refer :all]
            [testp.fibonacci :refer :all]))

(deftest test-my-then
  (is (= true (my-then true true)))
  (is (= false (my-then true false)))
  (is (= true (my-then false true)))
  (is (= true (my-then false false))))

(deftest test-factorial
  (is (= 1 (factorial 0)))
  (is (= 1 (factorial 1)))
  (is (= 2 (factorial 2)))
  (is (= 6 (factorial 3)))
  (is (= 24 (factorial 4)))
  (is (= 120 (factorial 5)))
  (is (= 720 (factorial 6))))

(deftest test-fibonacci
  (is (= 0 (fibonacci 0)))
  (is (= 1 (fibonacci 1)))
  (is (= 1 (fibonacci 2)))
  (is (= 2 (fibonacci 3)))
  (is (= 3 (fibonacci 4)))
  (is (= 5 (fibonacci 5)))
  (is (= 8 (fibonacci 6)))
  (is (= 13 (fibonacci 7)))
  (is (= 21 (fibonacci 8))))