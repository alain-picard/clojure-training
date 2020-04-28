(ns clojure-training.lesson04)

(def bank-account
  "An IDENTITY, which corresponds to a representation of the
   current VALUE of Bob's bank account.

   Values are immutable, but identities can take on a succession
   of differing values as time progresses in our system."
  (atom {:customer "Bob"
         :balance  50
         :updated-at (java.util.Date.)}))

(defn deposit!
  [account amount]
  (swap! account update :balance + amount))

;; What if we want to modify more than one field at a time?
;; We could try:

(defn deposit!
  [account amount]
  (swap! account update :balance + amount)             ; Don't do this!!!
  (swap! account assoc :updated-at (java.util.Date.))) ; What's wrong with this code?

(defn deposit!
  [account amount]
  (swap! account
         #(-> %
              (update :balance + amount)
              (assoc :updated-at (java.util.Date.)))))

;; Now observers of bank-account either see the old or the new value, never
;; an inconsistent balance and transaction time.


@bank-account
(deposit! bank-account 73)
@bank-account
