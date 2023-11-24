(ns clojure-mutest.utils 
  (:require [clojure.java.io :as io]))

(defn load-program-text [filename] 
  (slurp (io/resource filename)))