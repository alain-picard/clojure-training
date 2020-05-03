(ns lesson-3-answers)

;;; Enter your answers in here.

;;;; 4Clojuer question 128: Recognize Playing Cards

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
(assert (= (read-poker-hand "HK") {:suit :heart :rank 11}))

;;;; 4Clojure question 178: Best Hand

;; Fourth iteration: All conditions mapped

(defn best-hand-4 [hand]
  ;; getting suits and ranks counts for initial hand determination
  (let [[suit rank] (apply map vector hand)
        count-suits (frequencies suit)
        val-count-suits (count (frequencies suit))
        count-suits-count (count count-suits)
        count-ranks (frequencies rank)
        count-ranks-count (count count-ranks)
        val-count-ranks (vals (frequencies rank))
        count-val-count-ranks (count (vals (frequencies rank)))
        straight-rank (partition 5 1 [\A \2 \3 \4 \5 \6 \7 \8 \9 \T \J \Q \K \A])
        straight? (if (or (= (sort rank) [\2 \3 \4 \5 \A]) (= (sort rank) [\A \J \K \Q \T]))
                    true
                    (.contains straight-rank (sort rank)))
        ;;straight? (or (.contains straight-rank (sort rank)) [\2 \3 \4 \5 \A]) ;; this would not work, but worth keeping
        ]
    (cond
      ;; conditions explained, further differentiate will be done in the next iteration
      (and (= val-count-suits 1) (= straight? true)) :straight-flush
      (and (= count-ranks-count 2) (and (= count-val-count-ranks 2) (= (first val-count-ranks) 4))) :four-of-a-kind
      (and (= count-ranks-count 2) (and (= count-val-count-ranks 2) (= (first val-count-ranks) 3))) :full-house
      (= val-count-suits 1) :flush
      straight? :straight
      (and (= count-ranks-count 3) (and (= count-val-count-ranks 3) (= (first val-count-ranks) 3))) :three-of-a-kind
      (and (= count-ranks-count 3) (and (= count-val-count-ranks 3) (= (first val-count-ranks) 2))) :two-pair
      (= count-ranks-count 4) :pair
      :else :high-card)))

;; test for sanity
(= (best-hand-4 ["S2" "S3" "S4" "S5" "S6"]) :straight-flush)
(= (best-hand-4 ["SK" "DK" "HJ" "CK" "DK"]) :four-of-a-kind)
(= (best-hand-4 ["SJ" "DJ" "HJ" "D9" "H9"]) :full-house)
(= (best-hand-4 ["DQ" "DK" "DA" "D9" "DJ"]) :flush)
(= (best-hand-4 ["D5" "D2" "H3" "C4" "DA"]) :straight) ;; special straight
(= (best-hand-4 ["HA" "DK" "HQ" "HJ" "HT"]) :straight) ;; special straight
(= (best-hand-4 ["H6" "H7" "HT" "C9" "S8"]) :straight) ;; normal straight
(= (best-hand-4 ["SQ" "DQ" "HQ" "C9" "DJ"]) :three-of-a-kind)
(= (best-hand-4 ["DQ" "HQ" "CA" "HA" "DJ"]) :two-pair)
(= (best-hand-4 ["DQ" "HQ" "HA" "C9" "DJ"]) :pair)
(= (best-hand-4 ["HA" "D2" "H3" "C9" "DJ"]) :high-card)


;;;; Some tests that I do not want to delete
(comment

  (defn subseq? [sub] 
    (some #{sub} (partition 5 1 (map vector "A23456789TJQKA"))))

  (subseq? ["2" "3" "4" "5" "6"])

  (defn subseqq? [a b]
    (some #{a} (partition (count a) 1 b)))

  (subseqq? [1 2 3] [1 2 3 4 5 6])

  (map vector "A1234")

  (def hand (map list (vec (partition 5 1 [\A \2 \3 \4 \5 \6 \7 \8 \9 \T \J \Q \K \A]))))

  (class hand)

  (println hand)

  (def h [\2 \3 \4 \5 \6])

  (defn my-fun []
    (let [[suit rank] (apply map vector ["S4" "S3" "C5" "S2" "H6"])
          my-hand (partition 5 1 [\A \2 \3 \4 \5 \6 \7 \8 \9 \T \J \Q \K \A])]
      (println (class my-hand))
      (println (class (sort rank)))
      (println (.contains my-hand (sort rank)))))

  (my-fun))

;;(def s? (.contains hand rank))

;;(println s?)


;;(.contains (vec ([\A \2 \3 \4 \5] [\2 \3 \4 \5 \6])) [\2 \3 \4 \5 \6])

   
   
   
