(ns clojure-mutest.config
  (:require [clojure.edn :as edn]))

(def config-filename "./clojure-mutest-config.edn")

(def default-config {:mutators "all"
                     :check-tests-are-valid true
                     ;; should be valid path to file or to directory
                     :path nil
                     ;; argument for run-tests script
                     :run-tests-arg ""
                     ;; should be a valid path to file
                     :run-tests "./scripts/lein_run.sh"
                    ;;  output html path
                     :output-html "./output.html"})

(defn valid-file-path? [path]
  (and (string? path) (.exists (java.io.File. path))))

(defn check-config [config]
  (let [invalid-keys
        (filter (fn [[key value]]
                  (cond
                    (= key :path) (or (nil? value) (not (valid-file-path? value)))
                    (= key :run-tests) (not (valid-file-path? value))
                    :else false))
                config)]
    (if (empty? invalid-keys)
      true
      (do
        (println "Invalid configuration. The following keys have issues:")
        (doseq [[key value] invalid-keys]
          (println (str "  - " key ": " value)))
        (println "Probably you forgot to create configuration file" config-filename)
        false))))


(defn- read-config-file [file-path]
  (try
    (edn/read-string (slurp file-path))
    (catch Exception e
      (prn e)
      nil)))

(defn get-config []
  (let [config (merge default-config
                      (read-config-file config-filename))]
    (if (check-config config) config nil)))
