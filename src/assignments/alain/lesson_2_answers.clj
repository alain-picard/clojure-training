(ns lesson-2-answers)

;;;;  Anagram problem:
;;; http://www.4clojure.com/problem/77

(defn anagram?
  "Return true IFF the words A and B are anagrams of one another."
  [a b]
  (= (sort a)
     (sort b)))

(assert (anagram? "meat" "team"))

(assert (not (anagram? "meat" "teammeat")))

(defn add-anagram
  "Find which key of MAP, if any, is an anagram of WORD,
  and add WORD to the set of current anagrams under that key.
  If it's not an anagram of any key so far, add it to MAP
  under WORD as key with WORD being the only anagram in the set so far.

  The incoming map and resulting structure have this form:
  {\"team\" #{\"team\" \"mate\"}
   \"mite\" #{\"mite\"}}"
  [map word]
  (if-let [match (first (filter (partial anagram? word) (keys map) ))]
    (update map match conj word)
    (assoc map word #{word})))

(defn anagrams-problem-77
  "From a sequence of WORDS, output a set containing
  sets of anagrams found in the sequence.
  See http://www.4clojure.com/problem/77"
  [words]
  (->> (reduce add-anagram {} words)
       vals
       (filter #(> (count %) 1)) ; Spec only wanted collections of 2 or more anagrams
       set)) ; and wanted result in a set.

(assert
 (= (anagrams-problem-77 ["veer" "lake" "item" "kale" "mite" "ever"])
    #{#{"veer" "ever"} #{"lake" "kale"} #{"mite" "item"}}))

(assert
 (= (anagrams-problem-77 ["meat" "mat" "team" "mate" "eat"])
    #{#{"meat" "team" "mate"}}))



;;;;  Longest subseq problem:
;;; http://www.4clojure.com/problem/53
;;;;
;;
;; http://www.4clojure.com/problem/53
;; Longest subsequence

;; Given a vector of integers, find the longest consecutive
;; sub-sequence of increasing numbers. If two sub-sequences have the
;; same length, use the one that occurs first. An increasing
;; sub-sequence must have a length of 2 or greater to qualify.


(defn longest-subsequence [coll]
  (second
   (reduce
    (fn [[current best] item]
      ;; The accumulator need not be an atomic value!
      ;; Here we need to keep track of two things
      (if (and (seq current) (<= item (last current)))
        ;; end of sequence
        [[item] best]
        ;; we can keep going.  Have we beaten the best one yet?
        (let [current (conj current item)]
          (if (and (> (count current) 1) ; must be 2 or more entries to qualify
                   (> (count current) (count best)))
            [current current]
            [current best]))))
    [[] []]
    coll)))

(assert (empty? (longest-subsequence [7 6 5 4])))
(assert (= (longest-subsequence [1 0 1 2 3 0 4 5]) [0 1 2 3]))


;;; Implement `filter`

(assert (= (seq [1 5 3 9])
           (filter odd? [0 1 2 4 5 3 2 8 9 10])))


(defn our-filter-not-lazy [pred coll]
  (reverse ; To maintain order in which matching elements are seen
   (reduce (fn [acc x]
             (if (pred x)
               (conj acc x)
               acc))
           (seq [])
           coll)))

(assert (= (seq [1 5 3 9])
           (our-filter-not-lazy odd? [0 1 2 4 5 3 2 8 9 10])))


;; This is closer to how clojure really does it, with promise of laziness
;; on evaluating the predicate:
(defn our-filter-lazy [pred coll]
  (when (seq coll)
    (let [val (first coll)]
      (if (pred val)
        (cons val
              (lazy-seq (our-filter-lazy pred (rest coll))))
        (lazy-seq (our-filter-lazy pred (rest coll)))))))


(assert (= (seq [1 5 3 9])
           (our-filter-lazy odd? [0 1 2 4 5 3 2 8 9 10])))
