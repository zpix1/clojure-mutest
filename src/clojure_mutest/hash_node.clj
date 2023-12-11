(ns clojure-mutest.hash-node
  (:require [rewrite-clj.zip :as z]))

(defn- get-hash [type data]
  (.digest (java.security.MessageDigest/getInstance type) (.getBytes data)))


(defn- get-hash-str [data-bytes]
  (apply str
         (map
          #(.substring
            (Integer/toString
             (+ (bit-and % 0xff) 0x100) 16) 1)
          data-bytes)))

(defn sha1-hash [data]
  (get-hash-str (get-hash "sha1" data)))

(defn rm-whitespace [s]
  (-> (z/of-string* s)
      (z/postwalk (fn select [zloc] (z/whitespace? zloc))
                  (fn visit [zloc] (z/remove* zloc)))
      z/root-string))

(defn string-hash [source-str] (-> source-str
                                   rm-whitespace
                                   sha1-hash))