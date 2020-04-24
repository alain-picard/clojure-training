(ns clojure-training.live-lesson03)


;; Anagram problem:
;; http://www.4clojure.com/problem/77

(defn anagram? [a b]
  (= (set a)
     (set b)))


(assert (anagram? "meat" "team"))

#_
(assert (not (anagram? "meat" "teammeat")))

;; So then I tried to fix it by also requiring same number of characters:
(defn anagram? [a b]
  (and (= (set a)
          (set b))
       (= (count a)
          (count b))))

;; This solves the problem, but seemed ugly... then it occurred to me that:

;; Final solution:
(defn anagram?
  "Return true IFF the words A and B are anagrams of one another."
  [a b]
  (= (sort a)
     (sort b)))

;; I looked at the problem definition, which transformed
["veer" "lake" "item" "kale" "mite" "ever"]
;; into
#{#{"veer" "ever"} #{"lake" "kale"} #{"mite" "item"}}
;; And imagined that to do this, at some point there would
;; be an underlying representation which looked like this:

{"team" #{"team"}
 "mite" #{"mite"}}

;; and that when we try to add the word "mate", it'd have
;; to turn into this:

{"team" #{"team" "mate"}
 "mite" #{"mite"}}


;; So I pretended I already had this structure, and
;; wrote the "add one more word" step to it manually:
(let [m {"team" #{"team"}
         "mite" #{"mite"}}
      w "item"]
  (let [[match] (filter (partial anagram? w) (keys m) )]
    (if match
      (update m match conj w)
      (assoc m w #{w}))))

;; Once we have the above, we can wrap it in a function
(defn add-anagram [m w]
  (let [[match] (filter (partial anagram? w) (keys m) )]
    (if match
      (update m match conj w)
      (assoc m w #{w}))))

;; I'm not completely happy with the above, so now
;; I'd add a docstring and tweak the code for readability
;; Careful! In this function I shadowed the symbol `map`.
;; The `map` passed in is received as a hash map value, NOT
;; as the clojure.core/map function.
;; This is the result of clojure being what is called a "LISP-1".
;;
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

;; Now I'd go back and test it using my original code:
(let [m {"team" #{"team"}
         "mite" #{"mite"}}
      w "item"]
  (add-anagram m w)) ; Still works! cool!


;; Now it's a simple matter of reducing over our desired sequence:
(reduce add-anagram
        {}
        ["veer" "lake" "item" "kale" "mite" "ever"])
;; Seems to work, so, assemble in final form

(defn anagrams-problem-77 [words]
  (->> (reduce add-anagram {} words)
       vals
       (filter #(> (count %) 1)) ; Spec only wanted collections of 2 or more anagrams
       set)) ; and wanted result in a set.

(anagrams-problem-77 ["meat" "mat" "team" "mate" "eat"])

(assert
 (= (anagrams-problem-77 ["veer" "lake" "item" "kale" "mite" "ever"])
    #{#{"veer" "ever"} #{"lake" "kale"} #{"mite" "item"}}))

(assert
 (= (anagrams-problem-77 ["meat" "mat" "team" "mate" "eat"])
    #{#{"meat" "team" "mate"}}))

;;; Notice that I did not build the solution in one go,
;;; and that I used helper functions along the way.

;;; To recapitulate; here is what I would check in source
;;; control in a real project:

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
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
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


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


;; The accumulator need not be an atomic value!
;; Here we need to keep track of two things
(let [[current best] [[1 2] [7 8 ]]
      item 3]
  (
   (fn [[c b] i]
     (if (<= item (last current))
       ;; end of sequence
       [[item] best]
       ;; we can keep going.  Have we beaten the best one yet?
       (let [current (conj current item)]
         (if (> (count current) (count best))
           [current current]
           [current best]))))
   [current best]
   item))

(defn longest-subsequence [coll]
  (second
   (reduce
    (fn [[current best] item]
      (if (and (seq current) (<= item (last current)))
        ;; end of sequence
        [[item] best]
        ;; we can keep going.  Have we beaten the best one yet?
        (let [current (conj current item)]
          (if (and (> (count current) 1) ; must be 2 or more entries to qualify
                   (> (count current) (count best)))
            [current current]
            [current best]))))
    [[] []] coll)))


(longest-subsequence [7 6 5 4])
(longest-subsequence [2 3 3 4 5])
(assert (= (longest-subsequence [1 0 1 2 3 0 4 5]) [0 1 2 3]))


;;;; Implement `filter`

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
