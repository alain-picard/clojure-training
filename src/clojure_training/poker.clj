(ns clojure-training.poker)

(def suits
  "The valid suits in a standard deck."
  {\H :heart
   \S :spade
   \D :diamond
   \C :club})

;; Note, this is as problem 128 defines it; I, however, would not
;; do this; I would define 2->10 to be worth, well, 2->10,
;; with ace being either 1 or 14 depending on context, and the
;; face cards being 11,12,13.  But anyway.
(def ranks
  "The valid ranks in a standard deck."
  (zipmap
   [\2 \3 \4 \5 \6 \7 \8 \9 \T \J \Q \K \A]
   (range 13)))

(def face-value
  (zipmap (range 13)
          [:two :three :four :five :six :seven :eight :nine :ten
           :jack :queen :king :ace]))

(defn parse-card
  ""
  [s]
  (let [s    (clojure.string/upper-case s)
        suit (suits (first s))
        rank (ranks (second s))]
    (assert suit)
    (assert rank)
    {:suit suit :rank rank :face-value (face-value rank)}))

(assert (= (range 13) (map (comp :rank parse-card str)
                           '[S2 S3 S4 S5 S6 S7
                             S8 S9 ST SJ SQ SK SA])))


;;;; Recognizing hands

(defn parse-hand
  "Parse a collection of human readable card indicators,
  e.g. HQ  (for queen of heats), ST  (for ten of spades)
  and returns a collection of hand structures, in increasing
  order of rank."
  [coll]
  (let [hand (map parse-card coll)]
    (assert  (= 5 (count (set hand)))   ; No repeats!  No cheating!
             "Invalid hand.")
    (sort-by :rank hand)))

(defn card-frequencies [hand]
  ;; Returns things like #{3 2} for a full house.
  (->> (map :face-value hand)
       frequencies
       (sort-by  second >)
       (map second)))

(defn n-of? [n hand]
  (some #{n} (card-frequencies hand)))

(def pair?       (partial n-of? 2))
(def triple?     (partial n-of? 3))
(def quadruple?  (partial n-of? 4))
(def full-house? (every-pred pair? triple?))

(defn two-pair?  [hand]
  ;; Two pairs is slightly hairier; you have to see
  ;; if the frequencies contain 2 TWICE.
  (= [2 2]
     (filter #{2} (card-frequencies hand))))

(defn flush? [hand]
  (let [first-suit (:suit (first hand))
        same-suit? #(= (:suit %) first-suit)]
    (every? same-suit? hand))
  #_                                    ; Or, alternately
  (= 1                                  ; which is clearer?
     (count (set (map :suit hand)))))

(defn- regular-straight? [ranks]
  (let [low (first ranks)
        high (last ranks)]
    (= (set ranks)
       (set (range low (+ 1 high))))))

(defn- small-straight? [ranks]
  (= (set ranks)
     ;; Note the ace!
     #{12 0 1 2 3}))

(defn straight? [hand]
  (let [ranks (map :rank hand)]
    (or (regular-straight? ranks)
        (small-straight? ranks))))

(def straight-flush? (every-pred flush? straight?))

;;; Our final solutions:

(defn evaluate-poker-hand [cards]
  (let [hand (parse-hand cards)]
    (cond
      (straight-flush? hand) :straight-flush
      (quadruple? hand)      :four-of-a-kind
      (full-house? hand)     :full-house
      (flush? hand)          :flush
      (straight? hand)       :straight
      (triple? hand)         :three-of-a-kind
      (two-pair? hand)       :two-pair
      (pair? hand)           :pair
      :else                  :high-card)))

(and
 (= :high-card (evaluate-poker-hand ["HA" "D2" "H3" "C9" "DJ"]))
 (= :pair (evaluate-poker-hand ["HA" "HQ" "SJ" "DA" "HT"]))
 (= :two-pair (evaluate-poker-hand ["HA" "DA" "HQ" "SQ" "HT"]))
 (= :three-of-a-kind (evaluate-poker-hand ["HA" "DA" "CA" "HJ" "HT"]))
 (= :straight (evaluate-poker-hand ["HA" "DK" "HQ" "HJ" "HT"]))
 (= :straight (evaluate-poker-hand ["HA" "H2" "S3" "D4" "C5"]))
 (= :flush (evaluate-poker-hand ["HA" "HK" "H2" "H4" "HT"]))
 (= :full-house (evaluate-poker-hand ["HA" "DA" "CA" "HJ" "DJ"]))
 (= :four-of-a-kind (evaluate-poker-hand ["HA" "DA" "CA" "SA" "DJ"]))
 (= :straight-flush (evaluate-poker-hand ["HA" "HK" "HQ" "HJ" "HT"])))
