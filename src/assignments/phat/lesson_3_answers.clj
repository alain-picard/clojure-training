(ns src.assignments.phat.lesson-3-answers)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
;; 128 - Recognize Playing Cards

;; define maps for suit and rank
;; first part of the string is a key in suit
;; second part of the string is a key in rank

(defn recognize-card
  "Take a string and convert into a map of suit and rank"
  [card]
  (let [suit {"D" :diamond 
              "H" :heart 
              "C" :club 
              "S" :spade}
        card-rank {"2" 0 "3" 1 "4" 2 "5" 3 "6" 4 
                   "7" 5 "8" 6 "9" 7 "T" 8 "J" 9
                   "Q" 10 "K" 11 "A" 12}]
    {:suit (suit (str (first card)))
     :rank (card-rank (str (second card)))}))


;; tests
(= {:suit :diamond :rank 10} (recognize-card "DQ"))
(= {:suit :heart :rank 3} (recognize-card "H5"))
(= {:suit :club :rank 12} (recognize-card "CA"))
(= (range 13) (map (comp :rank recognize-card str)
                   '[S2 S3 S4 S5 S6 S7
                     S8 S9 ST SJ SQ SK SA]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; 178 - Best hand

(defn sort-cards
  "Recognize all cards and group cards by suit or rank"
  [card-coll keyword]
  (into (sorted-map) (group-by keyword (map recognize-card card-coll))))


(defn continuous-sequence?
  "check if a sequence increases with a step of 1"
  [coll]
  (apply = 1 (map - (rest coll) coll)))


(defn count-ranks
  "return a sequence of count of each card rank"
  [card-coll]
  (for [x (vals (sort-cards card-coll :rank))]
    (count x)))


(defn flush?
  "All cards in the same suit"
  [card-coll]
  (= 1 (count (keys (sort-cards card-coll :suit)))))


(defn straight?
  "All cards in sequence (aces can be high or low, but not both at once)"
  [card-coll]
  (let [seq (sort < (keys (sort-cards card-coll :rank)))]
    (if (= '(0 1 2 3 12) seq)
      true
      (continuous-sequence? seq))))

(defn straight-flush?
  "All cards in the same suit, and in sequence"
  [card-coll]
  (and (flush? card-coll) (straight? card-coll)))


(defn four-of-a-kind?
  "Four of the cards have the same rank"
  [card-coll]
  (some #(= 4 %) (count-ranks card-coll)))


(defn three-of-a-kind?
  "Three of the cards have the same rank"
  [card-coll]
  (some #(= 3 %) (count-ranks card-coll)))


(defn pair?
  "Two cards have the same rank"
  [card-coll]
  (= 1 ((frequencies (count-ranks card-coll)) 2)))


(defn full-house?
  "Three cards of one rank, the other two of another rank"
  [card-coll]
  (let [counts (count-ranks card-coll)]
    (and (pair? card-coll)
         (three-of-a-kind? card-coll))))


(defn two-pair?
  "Two pairs of cards have the same rank"
  [card-coll]
  (= 2 ((frequencies (count-ranks card-coll)) 2)))


(defn best-hand
  "take a collection of 5 cards  and determine the best poker hand"
  [card-coll]
  (cond
    (straight-flush? card-coll) :straight-flush
    (four-of-a-kind? card-coll) :four-of-a-kind
    (full-house? card-coll) :full-house
    (flush? card-coll) :flush
    (straight? card-coll) :straight
    (three-of-a-kind? card-coll) :three-of-a-kind
    (two-pair? card-coll) :two-pair
    (pair? card-coll) :pair
    :else :high-card))


;; tests
(= :high-card (best-hand ["HA" "D2" "H3" "C9" "DJ"]))
(= :pair (best-hand ["HA" "HQ" "SJ" "DA" "HT"]))
(= :two-pair (best-hand ["HA" "DA" "HQ" "SQ" "HT"]))
(= :three-of-a-kind (best-hand ["HA" "DA" "CA" "HJ" "HT"]))
(= :straight (best-hand ["HA" "DK" "HQ" "HJ" "HT"]))
(= :straight (best-hand ["HA" "H2" "S3" "D4" "C5"]))
(= :flush (best-hand ["HA" "HK" "H2" "H4" "HT"]))
(= :full-house (best-hand ["HA" "DA" "CA" "HJ" "DJ"]))
(= :four-of-a-kind (best-hand ["HA" "DA" "CA" "SA" "DJ"]))
(= :straight-flush (best-hand ["HA" "HK" "HQ" "HJ" "HT"]))

