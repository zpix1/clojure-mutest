(ns testp.core)

(defn my-then [a b] (or (boolean a) b))

(defn factorial [n]
  (if (< n 1)
    1
    (* n (factorial (dec n)))))