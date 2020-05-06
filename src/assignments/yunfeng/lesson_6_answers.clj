(ns assignments.yunfeng.lesson-6-answers
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

;;Non-lazy solution
(defn construct-regex
  "construct a regex which trys to find any part of the string which matches the regex"
  [regex]
  (re-pattern (str regex ".*")))

(defn grep
  "return a non-lazy result"
  [file-path regex]
  (with-open [reader (io/reader file-path)]
    (->> (filter #(re-matches (construct-regex regex) %) (line-seq reader))
         (into []))))

(grep "/Users/apple/Documents/GitHub/clojure-training/src/assignments/yunfeng/test.txt" #"aaa")


;;Two lazy solutions
;;First solution
(defn lazy-grep-1
  "return a lazy sequence result but it is loading everything in memory"
  [file-path regex]
  (->> (str/split (slurp file-path) #"\n")
       (filter #(re-matches (construct-regex regex) %))))

(lazy-grep-1 "/Users/apple/Documents/GitHub/clojure-training/src/assignments/yunfeng/test.txt" #"aaa")


;;Second solution
(defn- helper
  [rdr]
  (lazy-seq
   (binding [*in* rdr]
     (if-let [line (read-line)]
       (cons line (helper rdr))
       (do (.close rdr) (println "closed") nil)))))

(defn lazy-open
  [file]
  (lazy-seq
   (do (println "opening")
       (helper (io/reader file)))))

(defn lazy-grep-2
  "return a lazy sequence but the reader will not be closed until the last line is being read"
  [file-path regex]
  (filter #(re-matches (construct-regex regex) %) (lazy-open file-path)))

(lazy-grep-2 "/Users/apple/Documents/GitHub/clojure-training/src/assignments/yunfeng/test.txt" #"aaa")
