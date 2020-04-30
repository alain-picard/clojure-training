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

;; First iteration: Reading in a hand into suit sets and ranks sets

(defn best-hand-1 [hand]
  ;; Put both suits and ranks into sets for further processing
  (let [[suit rank] (apply map list hand)
        suit-set (set suit)
        rank-set (set rank)]
    (println suit-set)
    (println rank-set)))

;;(best-hand-1 ["DQ" "HK" "HA" "C9" "DJ"])

;; Second iteration: Start processing suit frequencies, realised should not put into sets

(defn best-hand-2 [hand]
  ;; getting suits and ranks counts for initial hand determination
  (let [[suit rank] (apply map vector hand)
        count-suits (frequencies suit)
        val-count-suits (vals (frequencies suit))
        count-suits-count (count count-suits)
        count-ranks (frequencies rank)
        count-ranks-count (count count-ranks)]
    ;; printing out just for testing
    (println suit)
    (println rank)
    (println count-suits)
    (println val-count-suits)
    (println count-suits-count)
    (println count-ranks)
    (println count-ranks-count)
    (cond
      ;; conditions explained, further differentiate will be done in the next iteration
      (= count-ranks-count 5) :flush-or-straight-flush
      (= count-ranks-count 4) :pair
      (= count-ranks-count 3) :two-pair-or-three-of-a-kind
      (= count-ranks-count 2) :four-of-a-kind-or-full-house
      (= count-suits-count 1) :flush-or-straight-flush)))

;; test for sanity
(= (best-hand-2 ["DQ" "HK" "HA" "C9" "DJ"]) :flush-or-straight-flush)
(= (best-hand-2 ["DQ" "HQ" "HA" "C9" "DJ"]) :pair)
(= (best-hand-2 ["DQ" "HQ" "CA" "HA" "DJ"]) :two-pair-or-three-of-a-kind)
(= (best-hand-2 ["SQ" "DQ" "HQ" "C9" "DJ"]) :two-pair-or-three-of-a-kind)
(= (best-hand-2 ["SK" "DK" "HK" "CK" "DJ"]) :four-of-a-kind-or-full-house)
(= (best-hand-2 ["SJ" "DJ" "HJ" "D9" "H9"]) :four-of-a-kind-or-full-house)
(= (best-hand-2 ["DQ" "DK" "DA" "D9" "DJ"]) :flush-or-straight-flush)

;; Third iteration: All conditions laid out, excepts for straight

(defn best-hand-3 [hand]
  ;; getting suits and ranks counts for initial hand determination
  (let [[suit rank] (apply map vector hand)
        count-suits (frequencies suit)
        val-count-suits (vals (frequencies suit))
        count-suits-count (count count-suits)
        count-ranks (frequencies rank)
        count-ranks-count (count count-ranks)
        val-count-ranks (vals (frequencies rank))
        count-val-count-ranks (count (vals (frequencies rank)))]
    ;; printing out just for testing
    (println "")
    (println "Hello this is a new hand")
    (println suit)
    (println rank)
    (println count-suits)
    (println val-count-suits)
    (println count-suits-count)
    (println count-ranks)
    (println count-ranks-count)
    (println "val-count-ranks" val-count-ranks)
    (println "count-val-count-ranks" count-val-count-ranks)
    (cond
      ;; conditions explained, further differentiate will be done in the next iteration
      (= count-ranks-count 5) :flush-or-straight-flush
      (= count-ranks-count 4) :pair
      (and (= count-ranks-count 3) (and (= count-val-count-ranks 3) (= (first val-count-ranks) 2))) :two-pair
      (and (= count-ranks-count 3) (and (= count-val-count-ranks 3) (= (first val-count-ranks) 3))) :three-of-a-kind
      (and (= count-ranks-count 2) (and (= count-val-count-ranks 2) (= (first val-count-ranks) 4))) :four-of-a-kind
      (and (= count-ranks-count 2) (and (= count-val-count-ranks 2) (= (first val-count-ranks) 3))) :full-house
      (= count-suits-count 1) :flush-or-straight-flush)))

;; test for sanity
(= (best-hand-3 ["DQ" "HK" "HA" "C9" "DJ"]) :flush-or-straight-flush)
(= (best-hand-3 ["DQ" "HQ" "HA" "C9" "DJ"]) :pair)
(= (best-hand-3 ["DQ" "HQ" "CA" "HA" "DJ"]) :two-pair)
(= (best-hand-3 ["SQ" "DQ" "HQ" "C9" "DJ"]) :three-of-a-kind)
(= (best-hand-3 ["SK" "DK" "HK" "CK" "DJ"]) :four-of-a-kind)
(= (best-hand-3 ["SJ" "DJ" "HJ" "D9" "H9"]) :full-house)
(= (best-hand-3 ["DQ" "DK" "DA" "D9" "DJ"]) :flush-or-straight-flush)

;; Fourth iteration: All conditions mapped

(defn subseq? [sub] 
  (some #{sub} (partition 5 1 (map vector "A23456789TJQKA"))))

(subseq? ["2" "3" "4" "5" "6"])

(defn subseqq? [a b]
  (some #{a} (partition (count a) 1 b)))

(subseqq? [1 2 3] [1 2 3 4 5 6])

(map vector "A1234")

(defn best-hand-4 [hand]
  ;; getting suits and ranks counts for initial hand determination
  (let [[suit rank] (apply map vector hand)
        count-suits (frequencies suit)
        val-count-suits (vals (frequencies suit))
        count-suits-count (count count-suits)
        count-ranks (frequencies rank)
        count-ranks-count (count count-ranks)
        val-count-ranks (vals (frequencies rank))
        count-val-count-ranks (count (vals (frequencies rank)))]
    ;; printing out just for testing
    (println "")
    (println "Hello this is a new hand-4")
    (println suit)
    (println rank)
    (println count-suits)
    (println "val-count-suits" val-count-suits)
    (println count-suits-count)
    (println count-ranks)
    (println count-ranks-count)
    (println "val-count-ranks" val-count-ranks)
    (println "count-val-count-ranks" count-val-count-ranks)
    (cond
      ;; conditions explained, further differentiate will be done in the next iteration
      (= count-ranks-count 5) :straight-flush
      (= count-ranks-count 4) :pair
      (and (= count-ranks-count 3) (and (= count-val-count-ranks 3) (= (first val-count-ranks) 2))) :two-pair
      (and (= count-ranks-count 3) (and (= count-val-count-ranks 3) (= (first val-count-ranks) 3))) :three-of-a-kind
      (and (= count-ranks-count 2) (and (= count-val-count-ranks 2) (= (first val-count-ranks) 4))) :four-of-a-kind
      (and (= count-ranks-count 2) (and (= count-val-count-ranks 2) (= (first val-count-ranks) 3))) :full-house
      (= count-suits-count 1) :flush
      :else :high-card)))

;; test for sanity
(= (best-hand-4 ["S2" "S3" "S4" "S5" "S6"]) :flush-or-straight-flush) ;;(= val-count-suits 5) and straight?
(= (best-hand-4 ["DQ" "HQ" "HA" "C9" "DJ"]) :pair)
(= (best-hand-4 ["DQ" "HQ" "CA" "HA" "DJ"]) :two-pair)
(= (best-hand-4 ["SQ" "DQ" "HQ" "C9" "DJ"]) :three-of-a-kind)
(= (best-hand-4 ["SK" "DK" "HK" "CK" "DJ"]) :four-of-a-kind)
(= (best-hand-4 ["SJ" "DJ" "HJ" "D9" "H9"]) :full-house)
(= (best-hand-4 ["DQ" "DK" "DA" "D9" "DJ"]) :flush-or-straight-flush) ;;straight?
(= (best-hand-4 ["DQ" "DK" "DA" "D9" "DJ"]) :high-card)


   
   
   
