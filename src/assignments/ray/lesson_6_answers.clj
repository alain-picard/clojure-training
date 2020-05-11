(ns lesson-6-answers)

;; multiple dispatch!

;; An interesting example: State machines

;; Consider a vending machine, which accepts coins and
;; drops a soda

(def vending-machine {:state :start :coins []})

(def price-of-soda 220)

(defn coin-value [machine] (reduce + (:coins machine)))

(ns-unmap *ns* 'handle)

(defmulti handle (fn [machine event] [(:state machine) (:type event)]))

(defmethod handle [:start :coin] [machine event]
  (println "Getting a coin in the start state: " (:coin-value event) " cents.")
  (-> machine
      (assoc :state :accumulating-money)
      (update :coins conj (:coin-value event))))

(defmethod handle [:accumulating-money :refund] [machine event]
  (println "Getting a coin in the start state: " event)
  (println "Refunding " (:coins machine))
  vending-machine)


;; Putting in money
(defmethod handle [:accumulating-money :coin] [machine event]
  (println "Getting a coin in the accumulating state: " event)
  (let [machine (-> machine
                    (assoc :state :accumulating-money)
                    (update :coins conj (:coin-value event)))]
    (if (< (coin-value machine) price-of-soda)
      machine
      (do                               ; We have enough money!
        (println "Disbursing delicious soda.")
        ;; Extension: Returning machine bacause we would always calculate changes after
        ;; The extension could have implemented here, but I do not want to change the function body
        machine))))

;;; Assignment: Extend this machine so it can make proper change
;;; if user pays too much.

(defmethod handle [:accumulating-money :change] [machine event]
  ;; If current amount < the price of one soda, do not make changes and return current machine
  ;; otherwise, print the differences as change and return a new vending-machine
  (println "Checking if any changes need to be made...")
  (let [total (coin-value machine)]
    (println "Current money in the vending machine: " total " cents.")
    (if (< total price-of-soda)
      (do
        (println "No changes, you can't even afford one soda.")
        machine)
      (do
        (println "Making changes of: " (- total price-of-soda) " cents.")
        vending-machine))))


;; A common pattern with state machines:

(let [events [{:type :coin :coin-value 20}
              {:type :coin :coin-value 20}
              {:type :change}
              {:type :coin :coin-value 200}
              {:type :change}
              {:type :coin :coin-value 200}
              {:type :coin :coin-value 200}
              {:type :coin :coin-value 10}
              {:type :change}]]
  ;; Now you can imagine events being an infinite lazy stream...
  ;; and this can be the main of the entire program.
  (reduce handle
          vending-machine
          events))
