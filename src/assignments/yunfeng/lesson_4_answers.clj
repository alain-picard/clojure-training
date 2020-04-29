(def suit-map {:S :spade :H :heart :D :diamond :C :club})
(def rank-map {:2 0 :3 1 :4 2 :5 3 :6 4 :7 5 :8 6 :9 7 :T 8 :J 9 :Q 10 :K 11 :A 12})

(defn parse-suit
  "parse the string to get the suit and return it as a keyword"
  [card-string]
  (keyword (subs card-string 0 1)))

(defn parse-rank
  "parse the string to get the rank and return it as a keyword"
  [card-string]
  (keyword (subs card-string 1)))

(defn recogize-a-card
  "use keywords to find its correspond values and construct the result"
  [card-string]
  {:suit ((parse-suit card-string) suit-map) :rank ((parse-rank card-string) rank-map)})

(recogize-a-card "ST")