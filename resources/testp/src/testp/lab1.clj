(ns testp.lab1)

(defn map' [f coll]
  (reduce
   (fn [acc x]
     (conj acc (f x)))
   [] coll))

(defn filter' [f coll]
  (reduce
   (fn [acc x]
     (if (f x) (conj acc x) acc))
   [] coll))

(defn helper
  [alphabet n last-letter]
  (if (zero? n)
    (list "")
    (apply
     concat
     (map' (fn [letter]
             (if (not= letter last-letter)
               (map' (fn [suffix]
                       (str letter suffix))
                     (helper alphabet (dec n) letter))
               nil))
           alphabet))))

(defn words-without-repeats
  [alphabet n]
  (helper alphabet n ()))