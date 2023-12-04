(ns testp.fibonacci)

(defn fibonacci [n]
  (if (< n 1)
    n
    (+ (fibonacci (- n 1)) (fibonacci (- n 2)))))