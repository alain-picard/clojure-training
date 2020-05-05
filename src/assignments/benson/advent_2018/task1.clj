(ns assignments.benson.advent_2018.task1)

(declare map-calculation)

(def input (slurp "src/assignments/benson/advent_2018/task1input"))

(def calculations (map read-string (clojure.string/split input #"\r\n")))

(defn calculate-frequency
  "Returns the summation of all frequencies"
  [calculations]
  (reduce + calculations))

; (calculate-frequency calculations) => 486



;; Always overflows???
(defn first-double-frequency-via-recursion
  "Returns the first resulting frequency which has already been calculated before"
  ([calculations]
   (first-double-frequency-via-recursion (cycle calculations) 0 #{0}))
  
  ([[calculation & others] total-value results]
   (let [new-total-value (+ total-value calculation)]  
     (if (contains? #{new-total-value} results)
       new-total-value
       (first-double-frequency-via-recursion others new-total-value (conj results new-total-value))))))

;; regular loop recursion doesn't
(defn first-double-frequency-via-loop
  "Returns the first resulting frequency which has already been calculated before"
  [calculations]
  (loop [calc-sequence (cycle calculations)
         results #{}
         total 0]
    (if (contains? results total)
      total
      (recur (rest calc-sequence)
             (conj results total)
             (+ total (first calc-sequence))))))

; (first-double-frequency-via-loop calculations) => 69285