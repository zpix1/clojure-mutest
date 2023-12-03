(ns clojure-mutest.core
  (:require [clojure-mutest.config :refer [get-config]]
            [clojure-mutest.logger :as log]
            [clojure-mutest.util :as util]
            [clojure.java.io :as io]
            [clojure.java.shell :as sh]))

(defn- test-runner [script arg]
  (fn [] (let [proc (sh/sh script arg)]
           (print "********** Running" script arg "**********"  "\n********** exit code" (:exit proc) "**********\n")
           (= (:exit proc) 0))))

(defn- process-directory [directory run-tests params]
  (let [files (filter #(.endsWith (.getName %) ".clj")
                      (file-seq (io/file directory)))]

    (->> (for [file files]
           (do
             (log/log-info "Working on " (.getPath file))
             (util/file-mutest (.getPath file) run-tests params)))
         flatten
         (reduce (fn [acc elem]
                  ;;  (util/use-output acc)
                   (assoc acc
                          :total (+ (:total acc) 1)
                          :killed (+ (:killed acc) (if (:killed elem) 1 0))
                          :mutants (cons elem (:mutants acc))))
                 {:total 0
                  :killed 0})
         util/use-output)))

(defn- run-for-config [config]
  (if (nil? config)
    (do (log/log-warning "Invalid config.") (System/exit 1)) nil)
  (let [path (:path config)
        run-tests (test-runner (:run-tests config) (:run-tests-arg config))
        check-tests-are-valid (:check-tests-are-valid config)]
    (if check-tests-are-valid
      (if (not (run-tests))
        (do (log/log-warning "Initial tests are not valid")
            (System/exit 1))
        (log/log-info "Initial tests are valid")))
    (process-directory path run-tests config))
  (shutdown-agents))

(defn -main []
  (log/print-clj-mutest-banner)
  (run-for-config (get-config)))