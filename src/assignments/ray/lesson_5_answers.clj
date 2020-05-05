(ns lesson-5-answers
  (:require
   [clojure.string :as s]
   [clojure.pprint :as pprint]
   [clojure.java.io :as io]))

;; Question 1: Write a unix-like grep command in Clojure

;; Defining the path to the text file
(def path "/Users/raywai/clojure-training/src/assignments/ray/test-file.txt")

;; Putting all lines in a vector
(defn read-path [some-path]
  ;; Takes in a string, returns a vector
  (with-open [rdr (io/reader some-path)]
    (into [] (line-seq rdr))))

;; Does the regex exist in a line?
(defn regex-exist? [regex string]
  ;; Takes in a regex and a string, returns a predicate value
  (if (re-seq (re-pattern regex) string) true false))

;; Grep command function
(defn grep [regex coll]
  ;; Takes in a string(regex) and a string(path), returns a vector
  (into [] (filter #(regex-exist? regex %) (read-path coll))))

(grep "Clojure" path)
(grep "I return an empty vector" path)



