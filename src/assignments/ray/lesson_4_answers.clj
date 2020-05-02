(ns lesson-4-answers)
(require '[clojure.string :as str])

;;;; Chapter 9 Answers


;;;; Chapter 10 Answers

;; Question 1

;; Define an atomic number to 0
(def a-number (atom 0))

;; Do some addition
(swap! a-number inc)
(swap! a-number inc)
(swap! a-number inc)

;; Printing out the atomic number
@a-number

;; Question 2

;; Count word frequencies

(defn get-quote []
  ;; Getting the quote from the site
  (slurp "https://www.braveclojure.com/random-quote"))

(defn clean-quote [quote]
  ;; A series of quote cleaning operations
  ;; 1. Make all words lower-case
  ;; 2. Splitting quote with author
  ;; 3. Getting the quote part from the split
  ;; 4. Returning all words
  (re-seq #"\w+" (first (str/split (str/lower-case quote) #"\n"))))

(defn word-frequencies [quote]
  ;; Getting the words frequencies in a quote
  (frequencies quote))

(time
 (defn quote-word-count [number-of-quotes]
   ;; Getting the resulting frequencies of words from quotes
   ;; Creating an atom of map
   ;; Uses futures to make the update calls to make sure the resulting atom value will not be returned prior all futures have finished
   ;; Users parallel map instead of regular map for performance
   (let [word-count (atom {})]
     (dorun (pmap (fn [nq] (swap! word-count (partial merge-with + (word-frequencies (clean-quote (get-quote)))))) (range number-of-quotes)))
     @word-count)))

(quote-word-count 8)

;; Some tests used while writing the solution which I don't want to delete them
(get-quote)
(clean-quote (get-quote))
(word-frequencies (clean-quote (get-quote)))

(def a1 (frequencies ["hello" "world"]))
(def a2 (frequencies ["bye" "world"]))
(def comb (merge-with + a1 a2))
(println a1)
(println a2)
(println comb)

;; Question 3

;; Defining a warrior and priest structure, each character has a map of hit-point and inventory
;; Hit-point represents another map which contains current hp and total hp, and the amount of hp as value
;; Inventory repressents a map with what items are inside inventory and value represnts the amount of the item
(def warrior (ref {:hit-point {:current 15 :total 40} :inventory {:healing-potion 0}}))
(def priest (ref {:hit-point {:current 30 :total 30} :inventory {:healing-potion 1}}))

(defn remove-potion [healer]
  ;; Reducing the healing-potion count inside inventory by 1
  (update-in healer [:inventory :healing-potion] dec))

(defn add-hp [taker]
  ;; If the difference between current hp and total hp is larger than the healing amount of 15,
  ;; add 15 to current hp, otherwise, top up current hp to total hp, this makes sure current hp is never about total hp
  (let [current-health (get-in taker [:hit-point :current])
        max-health (get-in taker [:hit-point :total])]
    (if (>= (- max-health current-health) 15)
      (update-in taker [:hit-point :current] + 15)
      (assoc-in taker [:hit-point :current] 40))))

(defn heal-up [healer taker]
  ;; First makes sure there is enough healing-potion before one can use it on another
  (if (> (get-in @healer [:inventory :healing-potion]) 0)
    (dosync
     (alter healer remove-potion)
     (alter taker add-hp))
    "Not enough potions."))

(println @priest)
(println @warrior)

(heal-up priest warrior)

(println @priest)
(println @warrior)





