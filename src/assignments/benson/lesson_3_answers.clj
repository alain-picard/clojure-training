(ns lesson-3-answers)


(defn get-suit
  "Returns the corresponding suit of a card based on LETTER"
  [letter]
  (case letter
    "S" :spade
    "H" :heart
    "D" :diamond
    "C" :club))

(defn get-rank
  "Returns the corresponding rank of a card based on VALUE"
  [value]
  (case value
    "2" 0
    "3" 1
    "4" 2
    "5" 3
    "6" 4
    "7" 5
    "8" 6
    "9" 7
    "T" 8
    "J" 9
    "Q" 10
    "K" 11
    "A" 12))

(defn derive-pair
  "Returns a map containing the suit and rank of a pair"
  [pair]
  (let [suit (subs pair 0 1) ; first letter of pair
        rank (subs pair 1 2)] ; second letter of pair
    {:suit (get-suit suit) :rank (get-rank rank)}))

; testing assert functionality while doing this exercise
(assert (= {:suit :diamond :rank 10} (derive-pair "DQ")))
(assert (= {:suit :heart :rank 3} (derive-pair "H5")))
(assert (= {:suit :club :rank 12} (derive-pair "CA")))
(assert (= (range 13) (map (comp :rank derive-pair str)
                           '[S2 S3 S4 S5 S6 S7
                             S8 S9 ST SJ SQ SK SA])))

