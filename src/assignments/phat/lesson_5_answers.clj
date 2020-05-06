(ns src.assignments.phat.lesson-5-answers
  (:require [clojure.java.io :as io]))

;; write a unix-like grep command in clojure, so that, e.g.
;; (grep "/tmp/somefile.txt" regex)
;; ["match 1" "match 2" "..."]

(def file "src/assignments/phat/tmp/somefile.txt")

(def regex-patterns {:url "^https?://[^/]+/([^/]+)/.*$"
                     :words "\\w+"
                     :digit "\\d+"})

(defn not-lazy-grep
  "Returns a sequence of matches of pattern in a file"
  [filepath regex]
  (assert (.exists (io/file filepath))
          "File path does not exist")
  (let [re (re-pattern regex)]
    (with-open [rdr (io/reader filepath)]
      (re-seq re (slurp rdr)))))

(not-lazy-grep file (:digit regex-patterns))


(defn lazy-grep 
  "Returns a lazy sequence of matches of a regex pattern from a file"
  [filepath regex]
  (assert (.exists (io/file filepath))
          "File path doesn't exist")
  (let [re (re-pattern regex)]
    ;; return a sequence of matches for each line in the file
    (with-open [rdr (io/reader filepath)] 
      (->> (line-seq rdr)
           (map #(re-seq re %))
           (remove nil?)
           (flatten)
           (doall)))))


;; i'm not too sure with the solution here since the doall forces the sequence resulted from map to be relized
;; but the function does returns a lazy sequence!!
(class (lazy-grep file "\\w+"))


;; the latter is more useful when we need to process a very large file as the function does not need to realize the input at the beginning


;; for extra difficulty, make it return a lazy sequence of matches instead. What difficulties does this present? Which version is better/more useful? 


;; Can you implement the former in terms of the latter? Discuss.
;; what if we wrap the former function in a lazy sequence? does this answers the question?

(defn lz-not-lazy-grep
  "Returns a lz-sequence of matches of pattern in a file"
  [filepath regex]
  (assert (.exists (io/file filepath))
          "File path does not exist")
  (let [re (re-pattern regex)]
    (lazy-seq
     (with-open [rdr (io/reader filepath)]
       (re-seq re (slurp rdr))))))

(class (lz-not-lazy-grep file "\\w+"))

;; ====================================================




