(ns assignments.benson.lesson-4-answers)

(declare parse-results map-futures get-quote-entry get-quote-author get-quote-count)

;;;; ---------------------------- CHAPTER 9 ------------------------------

;;;; EXERCISE 1 + 2

(def search-engines ["https://www.bing.com/search?q=" 
                     "https://au.search.yahoo.com/search?p="
                     "https://www.yippy.com/search?query="])

(defn search
  "Searchs SEARCH-TERM on provided SEARCH-ENGINES and returns the HTML
   of the search results"
  [search-term search-engines]
  ;; Mapping is done in parallel due to the large time of f
  (pmap #(slurp (str % search-term)) search-engines))

;;;; EXERCISE 3

(defn search2
  "Returns a vector of links from each search result"
  [search-term search-engines]
  (parse-results (search search-term search-engines)))

(defn parse-results
  "Parses all links from a vector of SEARCH-RESULTS
   Note: only the first 5 links are taken from each search result for
   ease of testing and validation purposes"
  [search-results]
  (map #(take 5 (re-seq #"https?://[^\"]*" %)) search-results))


;;;; ---------------------------- CHAPTER 10 ------------------------------

;;;; EXECRISE 1

(def gold (atom 0))

(swap! gold inc)
(swap! gold inc)
(swap! gold inc)

(str "I have: " (deref gold) " gold!") ; I have: 3 gold!

;;;; EXERCISE 2

(def random-quote-url "https://www.braveclojure.com/random-quote") ; URL to get random quote

(def quote-entries (atom {})) ; Atom map of entries of {AUTHOR-LAST-NAME QUOTE-WORD-COUNT}

(defn quote-word-count 
  "Returns a map of AMOUNT quote entries implemented as
  {AUTHOR-LAST-NAME QUOTE-WORD-COUNT}"
  [amount]
  (reset! quote-entries {})
  (let [futures (map-futures amount)]
    ;; Concurrently runs all futures
    (doseq [x futures] @x)
    @quote-entries))

(defn map-futures 
  "Creates a map of futures where each future is slurping a quote from 
   Random-quote-url and re-structuring the results to add to quote-entries"
  [amount]
  (reduce
   (fn [futures x]
     (conj futures 
           (future (swap! quote-entries conj 
                          (get-quote-entry (slurp random-quote-url))))))
   []
   (range amount)))

(defn get-quote-entry 
  "Returns a structured entry for quote-entries"
  [quote]
  {(get-quote-author quote) (get-quote-count quote)})


(defn get-quote-author 
  "Returns the last name or only provided name of QUOTE author"
  [quote]
  (-> quote
      (clojure.string/trim-newline)
      (clojure.string/trim)
      (clojure.string/split #" ")
      (last)))

(defn get-quote-count 
  "Returns the word count of QUOTE"
  [quote]
  (-> quote
      (clojure.string/split #" ")
      (count)))

;;;; EXERCISE 3

(def warrior (ref {:health 15}))
(def healer (ref {:potion 1}))

(defn use-potion
  "Transaction where OWNER uses a potion to heal TARGET to full health"
  [target owner]
  (if (> (:potion @healer) 0)
    (dosync
     (alter owner update :potion dec)
     (alter warrior assoc :health 40)))
  "Not enough potions!")

(use-potion warrior healer)
(deref warrior) ; =>{:health 40}
(deref healer) ; =>{:potion 0}