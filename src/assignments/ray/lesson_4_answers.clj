(ns lesson-4-answers)

;;;; Chapter 9 Answers


;;;; Chapter 10 Answers

;; Question 1

(def a-number (atom 0))

(swap! a-number inc)
(swap! a-number inc)
(swap! a-number inc)

@a-number

;; Question 2


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





