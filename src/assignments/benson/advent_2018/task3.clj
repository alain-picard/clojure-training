(ns assignments.benson.advent-2018.task3)

;;;; ------------------ Part 1 ------------------

(def task3input (slurp "src/assignments/benson/advent_2018/task3input"))

(def raw-claims (clojure.string/split-lines task3input))

;; split claims into a vector and convert all strings to integers
(def claims (vec (map #(vec 
                        (map (fn [measurement] (Integer/parseInt measurement)) 
                             (re-seq #"\d+" %))) 
                      raw-claims)))
;; claim = [ID X Y W H]
;; ID: The number of the claim
;; X: The number of inches between the left edge of the fabric and the left edge of the rectangle.
;; Y: The number of inches between the top edge of the fabric and the top edge of the rectangle.
;; W: The width of the rectangle in inches.
;; H: The height of the rectangle in inches.

;; ...........
;; ...........
;; ...#####...
;; ...#####...
;; ...#####...
;; ...#####...
;; ...........
;; ...........
;; ...........
; [3 2 5 4]

(defn parse-claim 
  "Returns a vector of the coordinates of fabric placement based on
   the claim"
  [[_ x y w h]]
  (for [width (range w)
        height (range h)]
    [(+ width x) (+ height y)]))

(defn parse-all-claims
  "Returns a vector of the coordinates of fabric placement of all CLAIMS"
  [claims]
  (reduce #(concat %1 (parse-claim %2)) [] claims))

(defn insert-claims 
  "Returns a map of all CLAIMS fabric placements.
   Any overlapping fabric placements will increment the value of the placement"
  [claims]
  (reduce #(if (contains? %1 %2)
             (assoc %1 %2 (inc (get %1 %2)))
             (assoc %1 %2 1))
          {} 
          claims))

(def fabric (insert-claims (parse-all-claims claims)))

(defn total-overlap 
  "Returns how many overalapping fabric placements there are"
  [fabric]
  (count (filter #(> (val %) 1) fabric)))

; (total-overlap fabric) => 107663

;;;; ------------------ Part 2 ------------------

(defn no-overlaps? 
  "Returns if the CLAIM causes no overlaps in FABRIC"
  [fabric claim]
  (empty? (remove true? (map #(= 1 (get fabric %)) (parse-claim claim)))))

(defn non-overlapping-claim 
  "Returns the CLAIM with no overlaps in FABRIC"
  [fabric claims]
  (remove nil? (for [claim claims]
                 (if (no-overlaps? fabric claim)
                   claim))))

; (non-overlapping-claim fabric claims) => ([1166 126 200 10 11])
; The ID is 1166