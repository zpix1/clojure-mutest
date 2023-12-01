(ns clojure-mutest.core
  (:require [clojure-mutest.util :refer [file-mutest]]
            [clojure.java.shell :as sh]))

(defn leingen-test-runner [path-to-project]
  (fn [] (let [proc (sh/sh "./scripts/lein_run.sh" path-to-project)]
           (prn proc)
           (= (:exit proc) 0))))

(defn -main []
  (let [run-tests (leingen-test-runner "./resources/testp")
        _ (prn run-tests)]
    (file-mutest "./resources/testp/src/testp/core.clj"
                 run-tests
                 nil)))