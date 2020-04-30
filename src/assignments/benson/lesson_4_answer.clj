(ns assignments.benson.lesson-4-answers
  (:import [java.net URL URLEncoder]))

;; Doesn't work
(def search-url "http://www.google.com/search?q=")

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

