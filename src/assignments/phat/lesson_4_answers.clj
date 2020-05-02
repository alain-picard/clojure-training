(ns src.assignments.phat.lesson_4_answers)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Chapter 9 exercises
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;
;; exercise 1
(def bing-url "https://www.bing.com/search?q=")
(def yahoo-url "https://au.search.yahoo.com/search?p=")
(def yippy-url "https://www.yippy.com/search?query=")

(defn search
  "return the first page of result from search engines"
  [text]
  (let [result (promise)]
    (doseq [search-tools [bing-url yahoo-url]]
      (future (if-let [results-page (slurp (str seacrh-tools text))]
                (deliver result results-page))))
    @result))

(def mypage (search "dogs"))
mypage

;;;;;;;;;;;;;
;; exercise 2
;; Update your function so it takes a second argument consisting of the search engines to use.
(def search-engines {:bing bing-url
                     :yahoo yahoo-url
                     :yippy yippy-url})

(defn search 
  [text & tools]
  (let [result (promise)]
    (doseq [tool tools]
      (assert (contains? search-engines tool)
              "Only valid search engines including :bing :yahoo and :yippy are accepted")
      (future (if-let [results-page (slurp (str (search-engines tool) text))]
                (deliver result results-page))))
    @result))

(search "cats" :bing :yahoo)

;;;;;;;;;;;;; 
;; exercise 3
;; Create a new function that takes a search term and search engines as arguments, and returns a vector of the URLs from the first page of search results from each search engine.
(defn results-from-search [search-term & engines]
  (let [results-page (search-on-engines search-term engines)
        matches (re-seq #"href=\"([^\" ]*)\"" results)
        uris (map second matches)
        external-links (filter #(re-find #"http(?s)://" %) uris)]
    external-links))










;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
 ;; Chapter 10 exercises
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;
;; exercise 1
;; Create an atom with the initial value 0, use swap! to increment it a couple of times, and then dereference it.

(def counter (atom 0))
(swap! counter inc)
(swap! counter inc)
(swap! counter inc)
(swap! counter inc)
(swap! counter inc)
@counter

;; just another example
(def person (atom {:name "Daniel"
                   :address "Sydney"
                   :age 0}))

(defn change-address
  [person new-address]
  (assoc person :address new-address))

(defn grow-up
  [person]
  (update-in person [:age] inc))

(swap! person change-address "Melbourne")
(swap! person grow-up)
@person
(swap! person grow-up)
(swap! person grow-up)
(swap! person change-address "Brisbane")
@person
(grow-up @person)
person

;;;;;;;;;;;;;
;; exercise 2

(defn count-word-url
  "count number of words in the quote returned from the url"
  [url]
  (let [quote (future (slurp url))]
    (count (clojure.string/split @quote #"\s"))))

(def url "https://www.braveclojure.com/random-quote")
(defn quote-word-count
  [n]
  (let [word-count (atom 0)
        urls (repeat n url)
        count-vec (map count-word-url urls)]
    (doseq [x count-vec]
      (swap! word-count #(+ % x)))
    @word-count))

(quote-word-count 3)

;;;;;;;;;;;;;
;; exercise 3

(def ironman (ref {:curr-hitpoint 15
                   :max-hitpoint 40
                   :inventory [:lasers :energy-blade]}))

(def doctor-strange (ref {:curr-hitpoint 20
                          :max-hitpoint 40
                          :inventory [:healing-potion :teleport :healing-potion]}))

(defn remove-item
  "remove an item from character`s inventory"
  [character item]
  (let [inventory (character :inventory)
        item-pos (.indexOf inventory item)]
    (vec (concat (subvec inventory 0 item-pos) (subvec inventory (inc item-pos))))))

(defn healing
  [giver taker]
  (dosync
   (when (some #(= :healing-potion %) (@giver :inventory))
     (alter giver assoc-in [:inventory] (remove-item @giver :healing-potion))
     (alter taker assoc-in [:curr-hitpoint] (@taker :max-hitpoint)))))

@ironman
@doctor-strange

; doctor-strange loses one :healing-potion from inventory
(healing doctor-strange ironman)
@doctor-strange
@ironman

; after this step, doctor-strange has no more :healing-potion in inventory
(healing doctor-strange doctor-strange)
(assert (= nil (@doctor-strange :healing-potion)))
@ironman
@doctor-strange

