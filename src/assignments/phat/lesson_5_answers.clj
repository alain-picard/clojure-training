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
  (let [re (re-pattern regex)]
    (with-open [rdr (io/reader filepath)]
      (re-seq re (slurp rdr)))))

(not-lazy-grep file (:digit regex-patterns))

(defn lazy-grep 
  "Returns a lazy sequence of matches of a regex pattern from a file"
  [filepath regex]
  (let [re (re-pattern regex)]
    ;; return a sequence of matches for each line in the file
    (with-open [rdr (io/reader filepath)] 
      (->> (line-seq rdr)
           (map #(re-seq re %))
           (remove nil?)
           (flatten)
           (doall)))))


;; i'm not too sure with the solution here since the doall forces the sequence resulted from map to be relized
;; but the function does return a lazy sequence!!
(class (lazy-grep file "\\w+"))


;; the latter is more useful when we need to process a very large file as the function does not need to realize the input at the beginning


;; for extra difficulty, make it return a lazy sequence of matches instead. What difficulties does this present? Which version is better/more useful? 


;; Can you implement the former in terms of the latter? Discuss.
;; what if we wrap the former function in a lazy sequence? does this answer the question?

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Day 1 - Advent of Code 2019
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;
;; part 1
;; problem description: https://adventofcode.com/2019/day/1
;; can not download input directly from this url: https://adventofcode.com/2019/day/1/input
;; so I have to save the input in a text file
(def file1 "src/assignments/phat/tmp/adventofcode-day1-input.txt")
(def input (map read-string (lazy-grep file1 "\\d+")))

(defn calculate-fuel
  [mass]
  (- (int (/ mass 3)) 2))

(defn calculate-fuel-sum
  "Takes a sequence of module's masses
   and returns sum of fuel requirements for all of the modules"
  [seq]
  (reduce + (map calculate-fuel seq)))

(calculate-fuel-sum input)

;;;;;;;;;
;; part 2
;; (defn total-module-fuel
;;   "Takes a module's mass and returns total fuel requirement for that module"
;;   [mass]
;;   (loop [fuel (calculate-fuel mass)
;;          total 0]
;;     (if (>= 0 fuel)
;;       total
;;       (recur (calculate-fuel fuel) (+ total fuel)))))

(defn total-module-fuel 
  "Takes a module's mass and returns total fuel requirement for that module"
  [mass]
  (->> mass
       (iterate calculate-fuel)
       (take-while #(<= 0 %))
       (rest)
       (reduce +)))

(defn total-added-fuel
  "Takes an input file including a sequence of module's masses
   and returns sum of fuel requirements including added fuel for all of the modules"
  [input]
  (let [module-masses (map read-string (lazy-grep input "\\d+"))]
    (reduce + (map #(total-module-fuel %) module-masses))))

(total-added-fuel inputfile)
; => 5105716

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Day 2 - Advent of Code 2019
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; problem description: https://adventofcode.com/2019/day/2

;;;;;;;;;
;; part 1
(def day2-input "src/assignments/phat/tmp/adventofcode-day2-input.txt")
(def input-seq (vec (map read-string (lazy-grep day2-input "\\d+"))))

(defn modify-value
  [coll pos val]
  (assoc coll pos val))

(defn intcode
  "Takes a sequence of integer, splits the sequence into sequences of 4 elements
  each smaller sequence is an instruction (an opcode + 3 parameters) of how the input sequence will be modified
  opcode=1 means adding param1 and param2
  opcode=2 means multiplying param1 and param2
  param1 and param2 indicates the position of values from the input sequence to be put into opcode operation
  param3 indicates the position of output from opcode operation to be stored in the input sequence"
  [seq]
  (let [input-seq (atom seq)]
    ;; by instructions
    ;; replace position 1 with the value 12 
    ;; and replace position 2 with the value 2
    (swap! input-seq modify-value 1 12)
    (swap! input-seq modify-value 2 2)
    
    (doseq [x (partition 4 @input-seq)
            :while (not= 99 (first x))]
      (let [first (first x)
            second (second x)
            third (nth x 2)
            output (cond (= 1 first) (+ (get @input-seq second)
                                       (get @input-seq third))
                         (= 2 first) (* (get @input-seq second)
                                       (get @input-seq third)))]
        (swap! input-seq modify-value (last x) output)))
    (first @input-seq)))

(intcode input-seq) ;=> 3516593

;; version 2 solution: try to solve the problem without using atom
;; not finished yet!
(defn opcode
  "Takes an input vector and an instruction such as [opcode param1 param2 param3], then returns a new vector which has values modified according to parameters in the instruction
  opcode=1 means adding param1 and param2
  opcode=2 means multiplying param1 and param2
  param1 and param2 indicates the position of values from the input sequence to be put into opcode operation
  param3 indicates the position of output from opcode operation to be stored in the input sequence"
  [seq ins]
  (if (= 99 (first ins))
    seq
    (let [opcode (first ins)
          param1 (second ins) ; position of first input value in seq
          param2 (nth ins 2)  ; position of second input value in seq
          param3 (last ins)   ; position of output where output will be stored in the seq
          output (cond (= 1 opcode) (+ (get seq param1)
                                       (get seq param2))
                       (= 2 opcode) (* (get seq param1)
                                       (get seq param2)))]
      (assoc seq param3 output))))



;;;;;;;;;
;; part 2
;; modify the previous function a bit
(defn intcode
  "Takes a sequence of integer, splits the sequence into sequences of 4 elements
  each smaller sequence is an instruction (an opcode + 3 parameters) of how the input sequence will be modified
  opcode=1 means adding param1 and param2
  opcode=2 means multiplying param1 and param2
  param1 and param2 indicates the position of values from the input sequence to be put into opcode operation
  param3 indicates the position of output from opcode operation to be stored in the input sequence" 
  [seq]
  (let [input-seq (atom seq)]
    (doseq [x (partition 4 @input-seq) ; produce instruction sequence
            :while (not= 99 (first x))]
      (let [first (first x)
            second (second x)
            third (nth x 2)
            output (cond (= 1 first) (+ (get @input-seq second)
                                       (get @input-seq third))
                         (= 2 first) (* (get @input-seq second)
                                       (get @input-seq third)))]
        (swap! input-seq modify-value (last x) output)))
    (first @input-seq)))

(defn find-pair
  "Finds a pair of values to store in index 1 and 2 of an intcode program so that the first index of the output from the intcode function will be equal to the target value"
  [seq target]
  (let [pairs (for [x (range 100)
                    y (range 100)]
                [x y])
        result (for [x pairs
                     :let [new-seq (assoc seq 1 (first x) 2 (second x))
                           output (intcode new-seq)]
                     :when (= target output)]
                 (+ (* 100 (first x)) (second x)))]
    result))

(find-pair input-seq 19690720)
;=> 7749
