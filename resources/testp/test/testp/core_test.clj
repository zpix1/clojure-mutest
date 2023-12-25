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

(deftest test-dummy-if-else
  (is (= 1 (dummy-if-else 1)))
  (is (= 2 (dummy-if-else 2)))
  (is (= 3 (dummy-if-else 3)))
  (is (= 4 (dummy-if-else 4)))
  (is (= 5 (dummy-if-else 5)))
  (is (= 6 (dummy-if-else 6)))
  (is (= 7 (dummy-if-else 7)))
  (is (= 8 (dummy-if-else 8))))

(deftest test-add-or-sum
  (is (= 6 (add-or-sum 1 5 true)))
  (is (= 4 (add-or-sum 2 2 false)))
  (is (= 7 (add-or-sum 3 4 true)))
  (is (= 4 (add-or-sum 4 1 false)))
  (is (= 12 (add-or-sum 5 7 true)))
  (is (= 48 (add-or-sum 6 8 false)))
  (is (= 13 (add-or-sum 7 6 true)))
  (is (= 8 (add-or-sum 8 1 false))))
