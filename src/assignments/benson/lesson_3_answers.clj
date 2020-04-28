(ns lesson-3-answers)

(declare flush? in-sequence?)

(defn get-suit
  "Returns the corresponding suit of a card based on the first
character of PAIR"
  [pair]
  (case (first pair)
    \S :spade
    \H :heart
    \D :diamond
    \C :club))

(defn get-rank
  "Returns the corresponding rank of a card based on the second
character of PAIR"
  [pair]
  (case (second pair)
    \2 0
    \3 1
    \4 2
    \5 3
    \6 4
    \7 5
    \8 6
    \9 7
    \T 8
    \J 9
    \Q 10
    \K 11
    \A 12))

(defn derive-pair
  "Returns a map containing the suit and rank from PAIR"
  [pair]
  {:suit (get-suit pair) :rank (get-rank pair)})

; testing assert functionality while doing this exercise
(assert (= {:suit :diamond :rank 10} (derive-pair "DQ")))
(assert (= {:suit :heart :rank 3} (derive-pair "H5")))
(assert (= {:suit :club :rank 12} (derive-pair "CA")))
(assert (= (range 13) (map (comp :rank derive-pair str)
                           '[S2 S3 S4 S5 S6 S7
                             S8 S9 ST SJ SQ SK SA])))


;Straight flush: All cards in the same suit, and in sequence
;Four of a kind: Four of the cards have the same rank
;Full House: Three cards of one rank, the other two of another rank
;Flush: All cards in the same suit
;Straight: All cards in sequence (aces can be high or low, but not both at once)
;Three of a kind: Three of the cards have the same rank
;Two pair: Two pairs of cards have the same rank
;Pair: Two cards have the same rank
;High card: None of the above conditions are met

;(= :straight-flush (__ ["HA" "HK" "HQ" "HJ" "HT"]))


(defn straight-flush?
  "Returns if all pairs in HAND are sequential and are of the same suit"
  [hand]
  (and (flush? hand) (in-sequence? hand))
  )

(defn in-sequence?
  "Returns whether the HAND is in sequence or not.
   This function expects the HAND to be sorted in descending order
   BUT has an exception for 'baby straights' where it expects a hand
   in ascending order of [ACE 2 3 4 5]"
  [hand]
  (let [values (map get-rank hand)]
    ;; If this is a 'baby straight'
    (if (and (= 12 (first values))
             (= (rest values) [0 1 2 3]))
      true
      ;; Else return if all values are sequential
      (every? #{-1} (map - (rest values) values))
      ))
  )

(defn flush?
  "Returns if all suits in the HAND are the same"
  [hand]
  (= 1 (count (set (map get-suit hand)))))