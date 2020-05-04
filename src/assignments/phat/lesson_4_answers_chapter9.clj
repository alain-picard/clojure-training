(ns src.assignments.phat.lesson_4_answers_chapter9)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Chapter 9 exercises
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;
;; exercise 1
(def search-engines {:bing "https://www.bing.com/search?q="
                     :yahoo "https://au.search.yahoo.com/search?p="
                     :yippy "https://www.yippy.com/search?query="})

(defn search
  "return the first page of result from search engines"
  [text]
  (let [result (promise)]
    (doseq [search-engine (vals search-engines)]
      (future (if-let [results-page (slurp (str search-engine text))]
                (deliver result results-page))))
    @result))

(search "dogs")


;;;;;;;;;;;;;
;; exercise 2
;; Update your function so it takes a second argument consisting of the search engines to use.
(defn selective-search 
  "return search results from selected search engines"
  [text & tools]
  (let [result (promise)]
    (doseq [tool tools]
      (assert (contains? search-engines tool)
              "Only valid search engines including :bing :yahoo and :yippy are accepted")
      (future (if-let [results-page (slurp (str (search-engines tool) text))]
                (deliver result results-page))))
    @result))

(selective-search "cats" :bing :yahoo)

;;;;;;;;;;;;; 
;; exercise 3
;; create a new function that takes a search term and search engines as arguments, and returns a vector of the URLs from the first page of search results from each search engine.

;; modify selective-search to take second argument as a list
(defn selective-search 
  "return search results from selected search engines"
  [text tools]
  (let [result (promise)]
    (assert (= clojure.lang.PersistentList (type tools))
            "Only accept list of search engines such as '(:bing :yahoo)")
    (assert (every? #(contains? search-engines %) tools)
            "List of search engines only accept valid values :bing :yahoo and :yippy")
    (doseq [tool tools]
      (future (if-let [results-page (slurp (str (search-engines tool) text))]
                (deliver result results-page))))
    @result))

(defn results-from-search 
  [text engines]
  (let [results (selective-search text engines)
        ;; regex search all strings starting with href
        matches (re-seq #"href=\"([^\" ]*)\"" results)
        ;; matches is a sequence of vectors
        ;; each vector has 2 elements
        ;; the second element is the target result url
        uris (map second matches)
        ;; target urls start with http/https
        links (filter #(re-find #"http(?s)://" %) uris)]
    links))

(results-from-search "cats" '(:bing :yahoo))


