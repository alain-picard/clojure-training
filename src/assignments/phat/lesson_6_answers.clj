(ns src.assignments.phat.lesson-6-answers
  (:require [clojure.java.io :as io]))

;; There are several changes I made to the code.
;; created some helper functions to return a collection of coins to make up a change amount
;; added some new methods which allow a buyer to confirm a purchse, then the machine will disburse the soda and print out coin values in a change amount

;; helper functions to make change money
(def coin-type '(500 200 100 50 20 10 5))

(defn next-coin-available
  "Returns the biggest coin value to add up to a change amount"
  [amount]
  (let [coins (filter #(>= amount %) coin-type)]
    (first coins)))

(defn change-money
  "Returns a collection of coins to make up an amount of change"
  [amount]
  (loop [remaining amount
         coins []]
    (if (zero? remaining)
      coins
      (recur (- remaining (next-coin-available remaining))
             (conj coins (next-coin-available remaining))))))

;; Consider a vending machine, which accepts coins and
;; drops a soda

(def vending-machine {:state :start :coins []})

(def price-of-soda 220)

(defn coin-value 
  [machine] 
  (reduce + (:coins machine)))

(ns-unmap *ns* 'handle)

(defmulti handle (fn [machine event] [(:state machine) (:type event)]))

(defmethod handle [:start :coin] [machine event]
  (println "Getting a coin in the start state:" (:coin-value event) "cents.")
  (-> machine
      (assoc :state :accumulating-money)
      (update :coins conj (:coin-value event))))


;; new method
(defmethod handle [:accumulating-money :buy-soda] [machine event]
  (cond 
    (< (coin-value machine) price-of-soda) (do (println "Not enough money") machine)
    (= (coin-value machine) price-of-soda) (do (println "Disbursing delicious soda") vending-machine)
    :else (do (println "Purchase confirmed! Disbursing delicious soda")
              (println "Change: " (change-money (- (coin-value machine) price-of-soda)))
              vending-machine)))


;; new method
(defmethod handle [:start :buy-soda] [machine event]
  (cond 
    (< (coin-value machine) price-of-soda) (do (println "Not enough money") machine)
    (= (coin-value machine) price-of-soda) (do (println "Disbursing delicious soda") vending-machine)
    :else (do (println "Purchase confirmed! Disbursing delicious soda")
              (println "Change: " (change-money (- (coin-value machine) price-of-soda)))
              vending-machine)))


;; If buyer overpays at the beginning and confirms the purchase
(-> vending-machine
    (handle {:type :coin :coin-value 300})
    (handle {:type :buy-soda}))


(defmethod handle [:accumulating-money :refund] [machine event]
  (println "Getting a coin in the start state: " event)
  (println (str  "Refunding: " (:coins machine)))
  vending-machine)


;; or he changes his mind -> refund
(-> vending-machine
    (handle {:type :coin :coin-value 20})
    (handle {:type :refund}))


(defmethod handle [:accumulating-money :coin] [machine event]
  (println "Getting a coin in the accumulating state: " event)
  (-> machine
      (assoc :state :accumulating-money)
      (update :coins conj (:coin-value event))))


;; buyer puts in more coins and confirms the purchase
(-> vending-machine
    (handle {:type :coin :coin-value 20})
    (handle {:type :coin :coin-value 250})
    (handle {:type :buy-soda}))


;; Add some more events
(let [events [{:type :coin :coin-value 20}
              {:type :coin :coin-value 20}
              {:type :coin :coin-value 200}
              {:type :buy-soda}
              {:type :coin :coin-value 200}
              {:type :refund}
              {:type :coin :coin-value 200}
              {:type :coin :coin-value 10}
              {:type :coin :coin-value 200}
              {:type :coin :coin-value 300}
              {:type :buy-soda}]]
  ;; Now you can imagine events being an infinite lazy stream...
  ;; and this can be the main of the entire program.
  (reduce handle
          vending-machine
          events))
