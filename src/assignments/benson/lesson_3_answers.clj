(ns assignments.benson.lesson-3-answers)

(declare flush? straight? filter-matching-pairs)

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


(defn straight-flush?
  "Returns if all pairs in HAND are sequential and are of the same suit"
  [hand]
  (and (flush? hand) (straight? hand)))

(defn full-house?
  "returns a truthy value if a full-house (3 pair + 2 pair) is in HAND
   otherwise return nil"
  [hand]
  (and (filter-matching-pairs hand 3)
       (filter-matching-pairs hand 2)))

(defn flush?
  "Returns if all suits in the HAND are the same"
  [hand]
  (= 1 (count (set (map get-suit hand)))))

(defn straight?
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
      (every? #{-1} (map - (rest values) values)))))

(defn four-of-a-kind?
  "Returns a list containing containing 4 matching pairs (truthy) otherwise return nil"
  [hand]
  (filter-matching-pairs hand 4))

(defn three-of-a-kind?
  "Returns a list containing containing 3 matching pairs (truthy) otherwise return nil"
  [hand]
  (filter-matching-pairs hand 3))

(defn two-pair?
  "Returns if there are two matching pairs in HAND"
  [hand]
  (= (count (filter-matching-pairs hand 2)) 2))

(defn pair?
  "Returns if there is only one maching pair in HAND"
  [hand]
  (= (count (filter-matching-pairs hand 2)) 1))

(defn get-matching-pairs
  "Returns a partitioned list of pairs by rank from HAND"
  [hand]
  (let [values (sort (map get-rank hand))]
    (partition-by identity values)))

(defn filter-matching-pairs
  "Returns a list with matching pairs with SIZE matches grouped together in nested lists
  otherwise return nil if no matching pairs are found"
  [matching-pairs size]
  (seq
   (filter #(= (count %) size)
           (get-matching-pairs matching-pairs))))

(defn best-hand
  "Returns the type of best hand available from HAND"
  [hand]
  (cond
    (straight-flush? hand) :straight-flush
    (four-of-a-kind? hand) :four-of-a-kind
    (full-house? hand) :full-house
    (flush? hand) :flush
    (straight? hand) :straight
    (three-of-a-kind? hand) :three-of-a-kind
    (two-pair? hand) :two-pair
    (pair? hand) :pair
    :else :high-card))