(ns testp.core)

(defn my-then [a b] (or (not a) b))

(defn factorial [n]
  (if (< n 1)
    1
    (* n (factorial (dec n)))))

(defn dummy-if-else [x] (if false 1 x))

