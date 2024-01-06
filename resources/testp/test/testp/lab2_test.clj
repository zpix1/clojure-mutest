(ns testp.lab2-test
  (:require [clojure.test :refer :all]
            [testp.lab2 :as lab2]))

(def ERR 0.000001)
(defn float-is [a b] (is (< (Math/abs (- a b)) ERR)))

(deftest a-test
  (testing "int of 2x from 0 to 10 is 100"
    (float-is (let [f (lab2/intop #(* %1 2))]
                (f 10))
              100))
  (testing "int of x^2 from 0 to 10.1 is 343"
    (float-is (let [f (lab2/intop #(* %1 %1))]
                (f 10.1))
              343.4505)))
