(ns assignments.benson.lesson-4-answers
  (:import [java.net URL URLEncoder]))

(declare map-futures get-quote-entry get-quote-author get-quote-count)

;; Doesn't work if you use '='
;; must use %3D instead
(def search-url "http://www.google.com/search?q%3D")

(defn google-search
  [search-term]
  (slurp (str search-url search-term)))

;; Code I stole online that works
(def google-search-url "http://www.google.com/search?q=")

(def user-agent "Chrome/25.0.1364.172")

(defn open-connection [url]
  (doto (.openConnection url)
    (.setRequestProperty "User-Agent" user-agent)))

(defn get-response [url]
  (let [conn (open-connection url)
        in   (.getInputStream conn)
        sb   (StringBuilder.)]
    (loop [c (.read in)]
      (if (neg? c)
        (str sb)
        (do
          (.append sb (char c))
          (recur (.read in)))))))

(defn search [query]
  (let [url (URL. (str google-search-url (URLEncoder/encode query) "&num=1"))]
    (get-response url)))

;(re-seq #"https?://[^\"]*" (search "clojure"))


;;;; CHAPTER 10

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