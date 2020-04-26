(ns lesson-2-answers)

;; Implements filter using a loop
(defn filter-loop
  [pred input]
  ;; recurses through input sequence and conjoins all variables
  ;; that pass the predicate condition
  (loop [xs input
         result []]
    (if xs
      (let [x (first xs)]
        (if (pred x)
          (recur (next xs) (conj result x))
          (recur (next xs) result)))
      result)))


;; Implements filter using reduce
(defn filter-reduce
  [pred input]
  (reduce
   (fn [output x]
     ;; Conjoin all values that pass the predicate condition
     (if (pred x)
       (conj output x)
       output))
   []
   input))


;; Implements update-in function
(defn update-in-custom
  [m k f]
  ;; uses assoc-in but applies inputted function
  (assoc-in
   m k
   (f (get-in m k))))


;; Finds and returns sets of anagrams encapsulated in a set
(defn find-anagram
  [words]
  (->> (reduce
        ;; Function that associates the original word to a key made from the sorted
        ;; version of that word
        (fn [anagrams word]
          (let [sorted-key (sort word)] ; sorted word to be used as a key
            (assoc
             anagrams
             sorted-key
             ;; Conjoins the word with its respective key and converts the value into a set
             (conj (set (anagrams sorted-key)) word))))
        {}
        words)
       ;; Filter the values to sets that have 2 or more words (existing anagrams)
       ;; and puts the entire sequence into a set
       (vals)
       (filter #(>= (count %) 2))
       (into #{})))