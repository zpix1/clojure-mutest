(ns clojure-mutest.core-test
  (:require [clojure-mutest.core :refer :all]
            [clojure-mutest.utils :refer [load-program-text]]
            [clojure.test :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (println "Program text:")
    (println (load-program-text "lab2.clj"))
    (println "Program sexpr:")
    (println (test0 (load-program-text "lab2.clj")))))
