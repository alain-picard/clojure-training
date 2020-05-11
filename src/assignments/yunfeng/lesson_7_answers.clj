;; An interesting example: State machines

;; Consider a vending machine, which accepts coins and
;; drops a soda

(def vending-machine {:state :start :coins []})

(def price-of-soda 220)

(defn coin-value [machine] (reduce + (:coins machine)))

(ns-unmap *ns* 'handle)

(defmulti handle (fn [machine event] [(:state machine) (:type event)]))

;;Methods changed in the assignment.
(defmethod handle [:accumulating-money :refund]
  [machine event]
  (println "Current coin value in the accumulating state: " (coin-value machine) "cents.")
  (println "Refunding " (coin-value machine) " cents.")
  vending-machine)

(defmethod handle [:start :coin]
  "Even in the start state it needs to check if the coin value is enough for a soda. And refunding happens automatically."
  [machine event]
  (println "Getting a coin in the start state: " (:coin-value event) " cents.")
  (let [machine (-> machine
                    (assoc :state :accumulating-money)
                    (update :coins conj (:coin-value event)))]
    (cond (< (coin-value machine) price-of-soda) machine
          (= (coin-value machine) price-of-soda) (do (println "Disbursing delicious soda.") vending-machine)
          (> (coin-value machine) price-of-soda) (do (println "Disbursing delicious soda.") (println "Refunding " (- (coin-value machine) price-of-soda) " cents.") vending-machine))))

(defmethod handle [:accumulating-money :coin]
  "Similar to the start state and refunding happens automatically."
  [machine event]
  (let [machine (-> machine
                    (update :coins conj (:coin-value event)))]
    (println "Getting a coin in the accumulating state: " (coin-value machine) " cents.")
    (cond (< (coin-value machine) price-of-soda) machine
          (= (coin-value machine) price-of-soda) (do (println "Disbursing delicious soda.") vending-machine)
          (> (coin-value machine) price-of-soda) (do (println "Disbursing delicious soda.") (println "Refunding " (- (coin-value machine) price-of-soda) " cents.") vending-machine))))



;; A common pattern with state machines:
(let [events [{:type :coin :coin-value 20}
              {:type :coin :coin-value 20}
              {:type :coin :coin-value 200}
              {:type :coin :coin-value 200}
              {:type :coin :coin-value 200}
              {:type :coin :coin-value 10}
              {:type :refund}]]
  ;; Now you can imagine events being an infinite lazy stream...
  ;; and this can be the main of the entire program.
  (reduce handle
          vending-machine
          events))
