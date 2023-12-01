(ns testp.core-test
  (:require [clojure.test :refer :all]
            [testp.core :refer :all]))

(deftest a-test
  (testing "My Then"
    (is (= (my-then false true) true))))
