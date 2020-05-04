(require '[clojure.string :as string])

;;Chapter 9 answers
;;Question 1
(defn bing-search
  [search-string]
  (let [result (future (slurp (str "https://www.bing.com/search?q=" search-string)))]
    (deref result 5000 "time out")))

;;test
(bing-search "game")


;;Question 2
(defn search-with-engine
  [search-string search-engine]
  (cond (= search-engine "Yahoo") (let [result (future (slurp (str "https://au.search.yahoo.com/search?p=" search-string)))]
                                    (deref result 5000 "time out"))
        (= search-engine "yippy") (let [result (future (slurp (str "https://www.yippy.com/search?query=" search-string)))]
                                    (deref result 5000 "time out"))
        :else (let [result (future (slurp (str "https://www.bing.com/search?q=" search-string)))]
                (deref result 5000 "time out"))))

;;test
(search-with-engine "hehe" "Bing")


;;Question 3
(defn search-result-urls
  [search-string search-engine]
  (->> (search-with-engine search-string search-engine)
       (re-seq #"href=\"http[^\"<]*")
       (map #(subs % 6))
       (vec)))

;;test
(search-result-urls "hehe" "yippy")



;;Chapter 10 answers
;;Question 1
(def q1 (atom 0))
(swap! q1 (fn [current-value] (inc current-value)))
@q1


;;Question 2
(def total-word-count (atom 0))

(defn download-and-count-one-quote
  []
  (let [quote (slurp "https://www.braveclojure.com/random-quote")]
    (count (string/split quote #" "))))

(defn update-total-count
  []
  (future (let [quote-count (download-and-count-one-quote)]
            (swap! total-word-count (fn [current-value] (+ current-value quote-count))))))

(defn quote-word-count
  "create new threads recursively and after that deref it to make sure all futures are done before return the final result"
  [number-of-quotes]
  (if (<= number-of-quotes 0)
    true
    (let [create-new-download (update-total-count)]
      (quote-word-count (- number-of-quotes 1))
      @create-new-download
      @total-word-count)))

;;test
(time (quote-word-count 4))
(swap! total-word-count (fn [current-value] 0))


;;Question 3
(defn v-for-character-1
  [{:keys [health]}]
  (or (and (>= health 0)
           (<= health 40))
      (throw (IllegalStateException "Character1 is Healthy."))))

(defn v-for-character-2
  [{:keys [healing-potion]}]
  (or (and (>= healing-potion 0))
      (throw (IllegalStateException "No more healing potion."))))

(def character-1 (ref {:health 15}))
(def character-2 (ref {:healing-potion 1}))

(defn healing
  [person-1 person-2]
  (dosync ((alter person-1 #(assoc % :health (if (and (> (:health %) 30) (< (:health %) 40))
                                               40
                                               (+ 10 (:health %)))))
           (alter person-2 #(assoc % :healing-potion (- (:healing-potion %) 1))))))

(healing character-1 character-2)

;;test
@character-1
@character-2
