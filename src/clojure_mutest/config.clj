(ns clojure-mutest.config
  (:require [clojure.edn :as edn]
            [clojure-mutest.logger :as log]))

(def config-filename "./clojure-mutest-config.edn")
(def ignore-filename "./clojure-mutest-ignore.edn")

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

(def default-ignore {:ignore []})

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
        (println "Probably you forgot to create configuration file" config-filename "or did not configure it completely")
        false))))

(defn ignore-entry? [entry]
  (string? (:hash entry)))

(defn check-ignore [ignore]
  (prn ignore)
  true)


(defn read-config-file [file-path]
  (try
    (edn/read-string (slurp file-path))
    (catch Exception e
      (log/log-warning (.getMessage e))
      nil)))

(defn get-config []
  (let [config (merge default-config
                      (read-config-file config-filename))]
    (if (check-config config) config nil)))

(defn get-ignore []
  (let [config (merge default-ignore
                      (read-config-file ignore-filename))]
    (if (check-ignore config) config nil)))