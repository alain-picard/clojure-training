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


;; Implements get-in function
(defn get-in-custom
  ([m ks]
   (reduce get m ks))
  ;; Returns default message if result is nil
  ([m ks default]
   (let [result (reduce get m ks)]
     (if result
       result
       default))))


;; Finds and returns sets of anagrams encapsulated in a set
(defn group-by-anagram
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


;; Returns all consecutive subsequences as nested vectors
(defn get-consecutive-subseqs
  [seqs]
  (reduce
   ;; Function that groups subsequences as nested vectors together in a vector
   (fn [seqs number]
     ;; If the current number is larger than the previous number by one (in consecutive sequence)
     ;; Then conjoin it to the last nested vector
     ;; Else associate a new nested vector containing the number at the end
     (if (> number (last (last seqs)))
       (assoc-in seqs [(dec (count seqs))] (conj (last seqs) number))
       (assoc-in seqs [(count seqs)] [number])))
   [[(first seqs)]] ; Initialize the first vector
   (rest seqs))) ; Pass in the rest of the sequence

;; Returns the first instance of the longest subsequences
(defn longest-increasing-subseq
  [seqs]
  (as-> (get-consecutive-subseqs seqs) seqs ; consecutive subsequences to be filtered
    (first
     (filter 
      ;; Filters to largest subsequences 
      ;; and removes subsequences if the largest subsequence is size 1
      #(and 
        (= (count %) (count (last (sort seqs))))
        (> (count %) 1))
      seqs))
    ;; If there is no subsequence return an empty vector
    (if (nil? seqs)
      []
      seqs)))