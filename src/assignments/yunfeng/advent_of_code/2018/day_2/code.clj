(ns advent-of-code.2018.day-2.code
  (:require [clojure.java.io :as io]))

(defn frequency-accumulator
  "Return a new map of format like {:2 x :3 y} and help counter-for-23 to accumulate the result"
  [frequency-for-23 new-frequency]
  (let [frequency-vec (vals new-frequency)]
    (merge-with + frequency-for-23 {:2 (if (some #(= 2 %) frequency-vec) 1 0) :3 (if (some #(= 3 %) frequency-vec) 1 0)})))


(defn counter-for-23
  "Return a map of format like {:2 x :3 y} where x means the number of ID which contains exactly 2 of any letter. Same meaning for y."
  [reader]
  (reduce frequency-accumulator  {:2 0 :3 0} (map frequencies (line-seq reader))))


(defn part-1
  [file-path]
  (with-open [reader (io/reader file-path)]
    (let [counter-result (counter-for-23 reader)]
      (* (:2 counter-result) (:3 counter-result)))))

(part-1 "/Users/apple/Desktop/clojure-noob/src/advent_of_code/2018/day_2/input.txt")


(defn differ-by-one
  "Check if two strings are differ exactly by one"
  [string-1 string-2]
  (= 1 (loop [iteration 0 result 0]
         (if (nil? (get string-1 iteration))
           result
           (recur (inc iteration) (if (= (get string-1 iteration) (get string-2 iteration)) result (inc result)))))))

(defn construct-result
  "Construct the desire result which means remove the charater which is different and return the remaining charachters."
  [string-1 string-2]
  (loop [iteration 0]
    (if (nil? (get string-1 iteration))
      nil
      (if (= (get string-1 iteration) (get string-2 iteration))
        (recur (inc iteration))
        (str (subs string-1 0 iteration) (subs string-1 (inc iteration)))))))

(defn part-2
  [file-path]
  (let [result (promise)]
    (future (with-open [reader (io/reader file-path)]
              (let [data (into [] (line-seq reader)) line-count (count data)]
                (dotimes [i line-count]
                  (let [line-i (get data i)]
                    (dotimes [j (- line-count i)]
                      (let [line-j (get data (+ i j))]
                        (when (differ-by-one line-i line-j)
                          (deliver result (construct-result line-i line-j)))))))
                (deliver result "no available box ID found."))))
    (deref result 5000 "time out")))

(part-2 "/Users/apple/Desktop/clojure-noob/src/advent_of_code/2018/day_2/input.txt")
