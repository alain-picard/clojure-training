(ns advent-of-code.2018.day-1.code
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn part-1
  [file-path]
  (with-open [reader (io/reader file-path)]
    (reduce + (map edn/read-string (line-seq reader)))))

(part-1 "/Users/apple/Desktop/clojure-noob/src/advent_of_code/2018/day_1/input.txt")

(io/resours)
(defn part-2
  [file-path]
  (let [result (promise)]
    (future (with-open [reader (io/reader file-path)]
              (let [lines (into [] (line-seq reader)) count (count lines)]
                (loop [iteration 0 frequency-map {0 1} last-result 0]
                  (let [new-frequency (+ last-result (edn/read-string (get lines iteration)))]
                    (if (contains? frequency-map new-frequency)
                      (deliver result new-frequency)
                      (recur (if (= count (inc iteration)) 0 (inc iteration)) (assoc frequency-map new-frequency 1) new-frequency)))))))
    (deref result 5000 "time out")))

(part-2 "/Users/apple/Desktop/clojure-noob/src/advent_of_code/2018/day_1/input.txt")

