;; (ns simple-tasks.lab2)

(def STEP 0.1)

(defn clj-adapter [start step] (iterate #(+ %1 step) start))

(defn intop
  ([f] (intop f STEP))
  ([f step] (let [calc #(* (+ (f %1) (f %2)) (/ (- %2 %1) 2))
                  subs (map calc
                            (clj-adapter 0 step)
                            (clj-adapter step step))
                  integ (concat '(0) (reductions + subs))]
              (fn [x]
                (let [i (quot x step)
                      ival (nth integ (quot x step))
                      errval (calc (* i step) x)
                      res (+ ival errval)] (prn i ival errval res) res)))))

(def mem (memoize intop))