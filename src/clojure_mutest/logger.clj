(ns clojure-mutest.logger)

(defn print-clj-mutest-banner []
  (println " ██████╗██╗          ██╗      ███╗   ███╗████████╗███████╗███████╗████████╗
██╔════╝██║          ██║      ████╗ ████║╚══██╔══╝██╔════╝██╔════╝╚══██╔══╝
██║     ██║          ██║█████╗██╔████╔██║   ██║   █████╗  ███████╗   ██║
██║     ██║     ██   ██║╚════╝██║╚██╔╝██║   ██║   ██╔══╝  ╚════██║   ██║
╚██████╗███████╗╚█████╔╝      ██║ ╚═╝ ██║   ██║   ███████╗███████║   ██║
╚═════╝╚══════╝ ╚════╝       ╚═╝     ╚═╝   ╚═╝   ╚══════╝╚══════╝   ╚═╝

Clojure Mutest Framework v0.0.1
"))

(defn log-info [& messages]
  (apply println "[INFO   ] " messages))

(defn log-warning [& messages]
  (apply println "[WARNING] " messages))