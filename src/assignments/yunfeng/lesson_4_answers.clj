;;recogize a card problem
(def suit-map {:S :spade :H :heart :D :diamond :C :club})
(def rank-map {:2 0 :3 1 :4 2 :5 3 :6 4 :7 5 :8 6 :9 7 :T 8 :J 9 :Q 10 :K 11 :A 12})

(defn parse-suit
  "parse the string to get the suit and return it as a keyword"
  [card-string]
  (keyword (subs card-string 0 1)))

;;test parse-suit function
(parse-suit "ST")

(defn parse-rank
  "parse the string to get the rank and return it as a keyword"
  [card-string]
  (keyword (subs card-string 1)))

;;test parse-rank function
(parse-rank "ST")


(defn recogize-a-card
  "use keywords to find its correspond values and construct the result"
  [card-string]
  {:suit ((parse-suit card-string) suit-map) :rank ((parse-rank card-string) rank-map)})

;;test recongize-a-card function
(recogize-a-card "ST")
(recogize-a-card "SA")
(recogize-a-card "H4")



;;best hand problem
(defn remove-duplicate-ranks
  "It is trying to remove duplicate ranks in a hand regardless of what the suit is and return the counted result"
  [recongized-hand]
  (->> (map :rank recongized-hand)
       (set)
       (count)))

;;test remove-duplicate-ranks function
(remove-duplicate-ranks [{:suit :spade :rank 12} {:suit :club :rank 12} {:suit :heart :rank 12} {:suit :diamond :rank 12} {:suit :spade :rank 3}])
(remove-duplicate-ranks [{:suit :spade :rank 12} {:suit :club :rank 12} {:suit :heart :rank 12} {:suit :diamond :rank 11} {:suit :spade :rank 3}])
(remove-duplicate-ranks [{:suit :spade :rank 12} {:suit :club :rank 12} {:suit :heart :rank 10} {:suit :diamond :rank 11} {:suit :spade :rank 3}])
(remove-duplicate-ranks [{:suit :spade :rank 12} {:suit :club :rank 9} {:suit :heart :rank 10} {:suit :diamond :rank 11} {:suit :spade :rank 3}])


(defn find-duplicate-ranks
  "It is trying to find duplicate ranks regardless of what the suit is"
  [recongized-hand]
  (->> (map :rank recongized-hand)
       (frequencies)
       (filter #(-> % val (> 1)))
       (keys)))

;;test find-duplicate-ranks function
(find-duplicate-ranks [{:suit :spade :rank 12} {:suit :spade :rank 0} {:suit :spade :rank 1} {:suit :spade :rank 2} {:suit :spade :rank 3}])
(find-duplicate-ranks [{:suit :diamond :rank 0} {:suit :spade :rank 0} {:suit :spade :rank 1} {:suit :spade :rank 2} {:suit :spade :rank 3}])


(defn flush?
  "Check if a hand is a flush"
  [recongized-hand]
  (->> (map :suit recongized-hand)
       (set)
       (count)
       (= 1)))

;;test flush? function
(flush? [{:suit :spade :rank 12} {:suit :spade :rank 0} {:suit :spade :rank 1} {:suit :spade :rank 2} {:suit :spade :rank 3}])
(flush? [{:suit :club :rank 12} {:suit :spade :rank 0} {:suit :spade :rank 1} {:suit :spade :rank 2} {:suit :spade :rank 3}])


(defn straight?
  "Check if a hand is a straight
   A 2 3 4 5 is a special case"
  [recongized-hand]
  (let [sorted-rank (sort (map :rank recongized-hand))]
    (or (= 4 (- (last sorted-rank) (first sorted-rank))) (= [0 1 2 3 12] sorted-rank))))

;;test straight? function
(straight? [{:suit :spade :rank 12} {:suit :spade :rank 0} {:suit :spade :rank 1} {:suit :spade :rank 2} {:suit :spade :rank 3}])
(straight? [{:suit :spade :rank 4} {:suit :spade :rank 0} {:suit :spade :rank 1} {:suit :spade :rank 2} {:suit :spade :rank 3}])
(straight? [{:suit :spade :rank 5} {:suit :spade :rank 0} {:suit :spade :rank 1} {:suit :spade :rank 2} {:suit :spade :rank 3}])


(defn evaluate-hand
  "It uses the number of remaining ranks after removing all the duplicate ranks in a hand to seprate cases. 
   case 1:  If there are 5 remaining ranks(no two ranks are the same) which means this hand cannot be pair, 2 pair, 3 of a kind, 4 of a kind or full house.
            Then we can check if the hand is a flush or straight to determine the final result.
   case 2:  If there are 4 remaining ranks(1 duplicate ranks) which means this hand can only be pair.
   case 3:  If there are 3 remaining ranks(2 duplicate ranks) which means this hand can only be two pair or 3 of a kind.
            Then we can check if the not unique ranks are all the same to determine the result.
   case 4:  If there are 2 remaining ranks(3 duplicate ranks) which means this hand can only be full house or 4 of a kind.
            We use the same method as case 3 to determine the final result.
   default: means invalid input"
  [recongized-hand number-of-unique-rank]
  (case number-of-unique-rank
    5 (if (flush? recongized-hand)
        (if (straight? recongized-hand)
          :straight-flush
          :flush)
        (if (straight? recongized-hand)
          :straight
          :high-card))
    4 :pair
    3 (if (apply = (find-duplicate-ranks recongized-hand))
        :three-of-a-kind
        :two-pair)
    2 (if (apply = (find-duplicate-ranks recongized-hand))
        :four-of-a-kind
        :full-house)
    "Invalid Input"))


(defn best-hand
  "When trying to execute best-hand first it convert the string to a map using recogize-a-card
  then it calucate the number of unique rank 
  and then pass the result to evaluate-hand to get a keyword as result"
  [hand]
  (let [recongized-hand (map recogize-a-card hand)]
    (->>
     recongized-hand
     (remove-duplicate-ranks)
     (evaluate-hand recongized-hand))))

;;test best-hand function
(= :high-card (best-hand ["HA" "D2" "H3" "C9" "DJ"]))
(= :pair (best-hand ["HA" "HQ" "SJ" "DA" "HT"]))
(= :two-pair (best-hand ["HA" "DA" "HQ" "SQ" "HT"]))
(= :three-of-a-kind (best-hand ["HA" "DA" "CA" "HJ" "HT"]))
(= :straight (best-hand ["HA" "DK" "HQ" "HJ" "HT"]))
(= :straight (best-hand ["HA" "H2" "S3" "D4" "C5"]))
(= :flush (best-hand ["HA" "HK" "H2" "H4" "HT"]))
(= :full-house (best-hand ["HA" "DA" "CA" "HJ" "DJ"]))
(= :four-of-a-kind (best-hand ["HA" "DA" "CA" "SA" "DJ"]))
(= :straight-flush (best-hand ["SA" "S2" "S3" "S4" "S5"]))
