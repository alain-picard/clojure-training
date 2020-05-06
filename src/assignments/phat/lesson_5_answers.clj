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
(def inputfile "src/assignments/phat/tmp/adventofcode-day1-input.txt")

(defn calculate-fuel
  [mass]
  (- (int (/ mass 3)) 2))

(defn calculate-fuel-sum
  "Takes an input file including a sequence of module's masses
   and returns sum of fuel requirements for all of the modules"
  [input]
  (let [module-masses (map read-string (lazy-grep input "\\d+"))]
    (reduce + (map calculate-fuel module-masses))))

(calculate-fuel-sum inputfile)

;;;;;;;;;
;; part 2

(defn total-module-fuel
  "Takes a module's mass and returns total fuel requirement for that module"
  [mass]
  (loop [fuel (calculate-fuel mass)
         total 0]
    (if (>= 0 fuel)
      total
      (recur (calculate-fuel fuel) (+ total fuel)))))

(defn total-added-fuel
  "Takes an input file including a sequence of module's masses
   and returns sum of fuel requirements including added fuel for all of the modules"
  [input]
  (let [module-masses (map read-string (lazy-grep input "\\d+"))]
    (reduce + (map total-module-fuel module-masses))))

(total-added-fuel inputfile)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Day 2 - Advent of Code 2019
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; problem description: https://adventofcode.com/2019/day/2

;;;;;;;;;
;; part 1
(def day2-input "src/assignments/phat/tmp/adventofcode-day2-input.txt")
(def input-seq (vec (map read-string (not-lazy-grep day2-input "\\d+"))))

(defn modify-value
  [coll pos val]
  (assoc coll pos val))

(defn intcode
  "Takes a sequence of input and decodes it restore the gravity assist program to the \"1202 program alarm\" just before the last computer caught fire" 
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

;;;;;;;;;
;; part 2
;; didn't quite understand the specification in this part so I had to refer to some other sources and solutions such as this https://fossheim.io/writing/posts/adventofcode19-day2/

;; modify the previous function a bit
(defn intcode
  "Takes a sequence of input and decodes it restore the gravity assist program to the \"1202 program alarm\" just before the last computer caught fire" 
  [seq]
  (let [input-seq (atom seq)]
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

;; I don't make a function here yet since I haven't found a way to return the output from the nested loop
(loop [noun 0]
  (when (< noun 100)
    (loop [verb 0]
      (when (< verb 100)
        (let [new-seq (assoc input-seq 1 noun 2 verb)
              output (intcode new-seq)]
          (if (= 19690720 output)
            (println (+ (* 100 noun) verb))))
        (recur (+ verb 1))))
    (recur (+ noun 1)))) 
;=> 7749
