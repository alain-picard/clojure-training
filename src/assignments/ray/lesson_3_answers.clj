(ns lesson-3-answers)

;;; Enter your answers in here.

;;;; 4Clojuer question 128

;; First iteration: Examining the input and output of reading first and second

(defn read-poker-hand-1 [hand]
  {:suit (first hand) :rank (second hand)})

(read-poker-hand-1 "DQ")

;; Second iteration: Reading inputs and assigning them into a map

(defn read-poker-hand-2 [hand]
  (let [s (first hand) r (second hand)]
    (into {} [[:suit s] [:rank r]])))

(read-poker-hand-2 "DQ")

;; Third iteration: Testing how casting works

(defn test [hand]
  (into {} [[:suit (first hand)] [:rank (second hand)]]))

(test "DQ")

(into {} {:suit ({\S :spade \D :diamond \H :heart \C :club} \D)
          :rank ({\2 0 \3 1 \4 2 \Q 10} \Q)})

(defn read-poker-hand-3 [hand]
  (let [s (first hand) r (second hand)]
    (into {} [[:suit ({\S :spade \D :diamond \H :heart \C :club} s)]
              [:rank ({\2 0 \3 1 \4 2 \5 3 \6 4 \7 5 \8 6 \9 7 \T 8 \J 9 \Q 10 \K 11 \A 12} r)]])))

(read-poker-hand-3 "DQ")

;;;; Fourth iteration: Optimising code, adding comments

(defn read-poker-hand [hand]
  ;; Casting the first input character into :suit, and the second input character into :rank
  {:suit ({\S :spade \D :diamond \H :heart \C :club} (first hand))
   :rank ({\2 0 \3 1 \4 2 \5 3 \6 4 \7 5 \8 6 \9 7 \T 8 \J 9 \Q 10 \K 11 \A 12} (second hand))})

(= (read-poker-hand "DQ") {:suit :diamond :rank 10})

;;;; Pathetic attempts on writing some tests

(assert (= (read-poker-hand "DQ") {:suit :diamond :rank 10}))
(assert (= (read-poker-hand "DQ") {:suit :blah :rank 42}))


   
   
   
