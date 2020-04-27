(ns lesson-2-answers)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Version of filter using a loop form, then using reduce



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Implement your own version of update-in
(def users {:name {:firstname "James" :lastname "Smith"} :age 26})

(update-in users [:age] inc)

(defn new-updatein 
  [m k func]
  (assoc m k (func (k m))))

(= (update-in users [:age] inc)
   (new-updatein users :age inc))



;;;;;;;;;;;;;;;;;;;;;;
;; 77 - anagram finder

;; convert each item into a sorted list of characters
;; group words with the same length and char frequencies

(defn find-anagram 
  [vector] 
  (set 
   (filter #(>= (count %) 2) 
           (set (map set (vals (group-by sort vector)))))))

(= (find-anagram ["meat" "mat" "team" "mate" "eat"])
   #{#{"meat" "team" "mate"}})

(= (find-anagram ["veer" "lake" "item" "kale" "mite" "ever" "everever"])
   #{#{"veer" "ever"} #{"lake" "kale"} #{"mite" "item"}})



;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; 53 - longest sub-sequence
;; ref link: https://ericsomdahl.github.io/posts/2015-03-01-Fifty-four.html

;; generate all sub-sequences from the original sequence
;; only take increasing sub-sequences
;; group sub-sequences by length
;; take the longest sub-sequence from sequences

;; this function is borrowed from the reference link
(defn get-seq
  ([s] (get-seq s 1))
  ([s t] (cond
           (empty? s) nil
           (= (count s) t) (cons s (lazy-seq (get-seq (rest s) 1)))
           :else (cons (take t s) (lazy-seq (get-seq s (inc t)))))))


(defn check-inc-seq 
  [sequence]
  (apply < sequence))


(defn longest-incseq
  "return a map entry with largest key"
  [sequence]
  (last 
   (group-by count (filter check-inc-seq (get-seq sequence)))))


(defn longest-subseq 
  [s]
  (vec
   (first
    (val (longest-incseq s)))))


;; test
;; okay with the first 3 tests"
(= (longest-subseq [1 0 1 2 3 0 4 5]) [0 1 2 3])

(= (longest-subseq [5 6 1 3 2 7]) [5 6])

(= (longest-subseq [2 3 3 4 5]) [3 4 5])

;; fail at this test
;; since the get-seq never returns an empty array given the input array is not empty
(= (longest-subseq [7 6 5 4]) [])





