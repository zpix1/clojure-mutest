(ns clojure-mutest.config-test
  (:require [clojure.test :refer :all]
            [clojure-mutest.config :as config]))

(deftest config-test
  (testing "valid-file-path? test"
    (is (config/valid-file-path? "./resources/testp"))
    (is (not (config/valid-file-path? "")))
    (is (not (config/valid-file-path? nil)))
    (is (not (config/valid-file-path? 42)))
    (is (not (config/valid-file-path? {:key "value"})))
    (is (not (config/valid-file-path? [1 2 3]))))

  (testing "check-config test"
    (is (config/check-config {:path "./resources/testp"
                              :run-tests "./scripts/lein_run.sh"
                              :output-html "./output.html"}))
    (is (config/check-config {:path "./resources/testp"
                              :run-tests "./scripts/lein_run.sh"
                              :output-html "./output.html"
                              :unknown-key "value"}))
    (is (not (config/check-config {:path "./inresources/testp"
                                   :run-tests "./scripts/lein_run.sh"
                                   :output-html "./output.html"})))
    (is (not (config/check-config {:path "./resources/testp"
                                   :run-tests "./inscripts/lein_run.sh"
                                   :output-html "./output.html"}))))

  (testing "read-config-file test"
    (is (= (config/read-config-file "./clojure-mutest-config.edn")
           {:mutators ["and-or", "gt-gte", "lt-lte", "true-false", "plus-mul", "swap-zero",
                       "eq-noteq", "empty?-seq", "not-boolean", "replace-if-with-then",
                       "replace-if-with-else"]
            :check-tests-are-valid true
            :path "./resources/testp/src/testp"
            :run-tests "./scripts/lein_run.sh"
            :run-tests-arg "./resources/testp"
            :output-html "./output.html"
            :git-diff-output-dir "diffs"}))
    (is (nil? (config/read-config-file "./inclojure-mutest-config.edn")))
    (is (nil? (config/read-config-file "./nonexistent-config.edn"))))

  (testing "get-config test"
    (with-redefs [config/read-config-file (constantly {:path "./resources/testp"})]
      (is (= (config/get-config) {:path "./resources/testp"
                                  :mutators "all"
                                  :check-tests-are-valid true
                                  :run-tests-arg ""
                                  :run-tests "./scripts/lein_run.sh"
                                  :output-html "./output.html"})))))
