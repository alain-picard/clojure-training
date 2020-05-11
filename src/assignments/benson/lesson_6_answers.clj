(ns assignments.benson.lesson-6-answers)

(def pizza-machine {:state :start :coins []})

(def price-of-pepperoni-pizza 600)

(def refund {:type :refund})

(defn coin-value [machine] (reduce + (:coins machine)))

(defmulti handle
  "Handler for pizza-machine events"
  (fn [machine event] [(:state machine) (:type event)]))


;; I couldn't add any docstrings to defmethod so I've put a 
;; description of each function as a comment above it 

;; Changes state to :accumulating-money and adds the first coin in MACHINE
;; Returns updated MACHINE
(defmethod handle
  [:start :coin] 
  [machine event]
  (println "Getting a coin in the start state:" (:coin-value event) "cents")
  (-> machine
      (assoc :state :accumulating-money)
      (update :coins conj (:coin-value event))))

;; Adds coins to the MACHINE
;; When the total value of all coins reaches price-of-pepperoni-pizza
;; dispense the pizza
;; If the total amount of coins exceed price-of-pepperoni-pizza then
;; dispense the change too
;; Returns updated MACHINE
(defmethod handle [:accumulating-money :coin] [machine event]
  (println "Getting a coin in the accumulating-money state:" (:coin-value event) "cents")
  (let [machine (-> machine
                    (assoc :state :accumulating-money)
                    (update :coins conj (:coin-value event)))
        total-value (coin-value machine)]
    (if (< total-value price-of-pepperoni-pizza)
      machine
      (do
        (println "Disbursing pepperoni pizza")
        ;; If there is change left, disburse it
        (if (> total-value price-of-pepperoni-pizza)
          (println "Disbursing change:" (- total-value price-of-pepperoni-pizza)))
        pizza-machine))))

;; Refunds the coins inside MACHINE
;; Returns pizza-machine in its original state
(defmethod handle [:accumulating-money :refund] [machine event]
  (println "Getting a refund request in the accumulating-money state:" event)
  (println "Refunding" (coin-value machine))
  pizza-machine)

;; map for coin types, I don't know if it's a good idea to use '$' in keys
(def coin-types {:5c 5 :10c 10 :20c 20 :50c 50 :1$ 100 :2$ 200})

(defn coin
  "Return a coin event based on Australian coins
   Expected Args: [:5c :10c :20c :50c :1$ :2$]"
  [coin-type]
  {:type :coin :coin-value (coin-type coin-types)})

(defn coins
  "returns a collection of coin events based on Australian coins
   Expected Args: [:5c :10c :20c :50c :1$ :2$]"
  [coin-collection]
  (map coin coin-collection))

(assert (= (coin :20c) {:type :coin :coin-value 20}))


(defn handle-events 
  "Handles EVENTS for MACHINE and returns the final state of MACHINE"
  [machine & events]
  (reduce handle
          machine
          (flatten events)))

;; correct change
(handle-events pizza-machine (coins [:50c :50c :2$ :2$ :1$]))
; => Disbursing pepperoni pizza

;; refund
(handle-events pizza-machine (coins [:50c :50c :10c :5c]) refund)
; => Refunding 115

;; incorrect change
(handle-events pizza-machine (coins [:2$ :2$ :10c :20c :50c :2$]))
; => Disbursing pepperoni pizza
; => Disbursing change: 80