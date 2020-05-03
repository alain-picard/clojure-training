(ns src.assignments.phat.lesson_4_answers_chapter10)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
 ;; Chapter 10 exercises
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;
;; exercise 1
;; Create an atom with the initial value 0, use swap! to increment it a couple of times, and then dereference it.

(def counter (atom 0))
(swap! counter inc)
(swap! counter inc)
(swap! counter inc)
(swap! counter inc)
(swap! counter inc)
@counter

;; just another example
(def person (atom {:name "Daniel"
                   :address "Sydney"
                   :age 0}))

(defn change-address
  [person new-address]
  (assoc person :address new-address))

(defn grow-up
  [person]
  (update-in person [:age] inc))

(swap! person change-address "Melbourne")
(swap! person grow-up)
@person
(swap! person grow-up)
(swap! person grow-up)
(swap! person change-address "Brisbane")
@person
(grow-up @person)
person

;;;;;;;;;;;;;
;; exercise 2

(defn count-word-url
  "count number of words in the quote returned from the url"
  [url]
  (let [quote (future (slurp url))]
    (count (clojure.string/split @quote #"\s"))))

(def url "https://www.braveclojure.com/random-quote")
(defn quote-word-count
  [n]
  (let [word-count (atom 0)
        urls (repeat n url)
        count-vec (map count-word-url urls)]
    (doseq [x count-vec]
      (swap! word-count #(+ % x)))
    @word-count))

(quote-word-count 5)

;;;;;;;;;;;;;
;; exercise 3

(def ironman (ref {:curr-hitpoint 15
                   :max-hitpoint 40
                   :inventory [:lasers :energy-blade]}))

(def doctor-strange (ref {:curr-hitpoint 20
                          :max-hitpoint 40
                          :inventory [:healing-potion :teleport :healing-potion]}))

(defn remove-item
  "remove an item from character`s inventory"
  [character item]
  (let [inventory (character :inventory)
        item-pos (.indexOf inventory item)]
    (vec (concat (subvec inventory 0 item-pos) (subvec inventory (inc item-pos))))))

(defn healing
  [giver taker]
  (dosync
   (when (some #(= :healing-potion %) (@giver :inventory))
     (alter giver assoc-in [:inventory] (remove-item @giver :healing-potion))
     (alter taker assoc-in [:curr-hitpoint] (@taker :max-hitpoint)))))

@ironman
@doctor-strange

; doctor-strange loses one :healing-potion from inventory
(healing doctor-strange ironman)
@doctor-strange
@ironman

; after this step, doctor-strange has no more :healing-potion in inventory
(healing doctor-strange doctor-strange)
(assert (= nil (@doctor-strange :healing-potion)))
@ironman
@doctor-strange

