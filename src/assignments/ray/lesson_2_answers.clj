(ns lesson-2-answers)

;;; Enter your answers in here.

;;;; Question 1: Filter using loop

(defn filter-loop [pred coll]
  (loop [coll coll
         result []]
    ;; if collection is empty, return the resulting vector
    (if (= coll ())
      result
      ;; tail iteration with recur
      ;; if predicate function evaluates true, add the element to the result vector
      (recur (rest coll)
             (if (pred (first coll)) (conj result (first coll)) result)))))

(filter-loop even? (range 10))
(filter-loop odd? [1 2 3 4 5])

;;;; Question 2: Filter using reduce

(defn filter-reduce [pred coll]
  ;; applying the function to an empty list with each element from coll
  ;; our function: if the predicate function evaluates true, add the element to []
  (reduce (fn [result coll] (if (pred coll) (conj result coll) result))
          []
          coll))

(filter-reduce1 even? (range 10))
(filter-reduce odd? [1 2 3 4 5])

;;;; Question 3: Update-in implementation
(defn custom-update-in [coll ks func]
  (assoc coll (first ks) (func (get coll (first ks)))))

(def p {:name "Ray" :age 30})
(def t-map {:a {:b 3} :b 40})

(update-in p [:age] inc)
(update-in t-map [:a :b] inc)

(custom-update-in p [:age] inc)
;;(custom-update-in t-map [:a :b] 1)

;;;; Question 4: 4clojure question 77

(def words-1 ["meat" "mat" "team" "mate" "eat"])
(def words-2 ["veer" "lake" "item" "kale" "mite" "ever"])

;;;; Final anagram question answer

(defn find-anagram [words]
  ;; group by word frequencies fuction groups words that shared the same frequencies
  ;; essentially finding whether they are anagrams
  ;; the group-by function returns a map of key-value pairs, we then take the values, which
  ;; represents the words that share the same frequencies, hence, they are anagrams to each other
  (into #{} (filter #(> (count %) 1) (map set (vals (group-by frequencies words))))))

(find-anagram words-1)
(find-anagram words-2)

;;;; Clears 4clojure.com/problem/77

(fn [words]
  (into #{} (filter #(> (count %) 1) (map set (vals (group-by frequencies words))))))

;;;; Question 5: 4clojure question 53




