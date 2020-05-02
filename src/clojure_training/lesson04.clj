(ns clojure-training.lesson04)

;;;; Lesson plan:
;; - ask for completed assignments by lunchtime on the day
;;   of lesson, so I can review.
;;
;; - suggest 1 on 1
;;
;; - suggest partial solutions, leave questions in PRs or issues
;;
;; - ask for comments/questions on what they'd like to see
;;   in material or preparation.
;;
;; - Review a couple of misunderstandings:
;;   explain about maps, what's the key, what's the value,
;;   key isn't same as keyword,
;;   maps and sets (all colls, actually) as functions
;;
;; - Another misunderstanding about booleans:
;;   There is no virtue in returning true explicitly
;;   instead of "any truthy value".
;;
;; - Field questions about poker assignments.
;;
;; - review AP's solution, if interest is shown
;;
;; - get on with this lecture's new material!
;;
;; -



;;;; Atoms

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
  ;; The way we change the VALUE associated with the IDENTITY _account_
  ;; is by calling SWAP!
  ;; The bang (!) is mnemonic for "performs a mutable operation"
  ;; (in the way that (?) is mnemonic for "predicate".
  (swap! account update :balance + amount))


;; After doing a deposit:
(deposit! bank-account 73)
;; we can get back the NEW, IMMUTABLE value associated
;; with this identity:
(deref bank-account)
;; or, often, just
@bank-account ; for short.

;; The ONLY WAY of modifying the value associated with an identity
;; is via swap!, and observers (other threads doing (deref bank-account)
;; can only ever see a value which is before, or after, the function requested
;; by swap! completes.  No in-between.  We call this an ATOMIC swap.
;; (atomic --- from the greek: indivisible).


;;;;
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


;;;; Refs

;; What if we have to maintain _multiple_ identities in a consistent way?
;; e.g. what if we have a set of "Gold members", and you become a gold member
;; if your account has more than $1,000,000 ?

(defonce ^{:doc "The set of names of those most exalted of bank account holders."}
  gold-members
  (atom #{}))

;; We could write:
(defn deposit!
  [account amount]
  (swap! account
         #(-> %
              (update :balance + amount)
              (assoc :updated-at (java.util.Date.))))
  (when (> 1000000 (:balance @account))
    ;; What happens if another thread does a big withdrawal right here?
    (swap! gold-members conj (:customer @account))))

;; This is called a RACE CONDITION.
;; Databases handle this problem by a mechanism called a TRANSACTION,
;; where all changes are committed, or none are committed, and any
;; thread in a transaction sees (and keeps seeing) consistent values
;; until the end of the transaction, no matter what other threads are doing.

;; Clojure uses the same mechanism, with something called REFS.

;; We write, instead:

(def bank-account
  "An IDENTITY, which corresponds to a representation of the
   current VALUE of Bob's bank account.

   Values are immutable, but identities can take on a succession
   of differing values as time progresses in our system."
  (ref {:customer "Bob"
        :balance  50
        :updated-at (java.util.Date.)}))

(defonce ^{:doc "The set of names of those most exalted of bank account holders."}
  gold-members
  (ref #{}))

(defn deposit!
  [account amount]
  (dosync
   (alter account
          #(-> %
               (update :balance + amount)
               (assoc :updated-at (java.util.Date.))))
   (when (> 1000000 (:balance @account))
     ;; What happens if another thread does a big withdrawal right here?
     ;; One of 2 things:
     ;; They block (we win, and continue), OR
     ;; clojure throws an exception, and RETRIES the entire
     ;; DOSYNC block, until we can act on consistent values.
     (alter gold-members conj (:customer @account)))))

;;; NOTE: the retries means that you can't do any side-effects
;;; inside a dosync without running the risk of having those
;;; side-effects run multiple times.

;; The io! macro
(comment
  ;; Do try this at home
  (dosync
   (io!
    (alter gold-members conj "Alain"))))


;;;; Agents

;; Allow uncoordinated, atomic change

(def james-bond (agent {:kills 0 :victims #{}}))

(defn murder [spy victim]
  (-> spy
      (update :kills inc)
      (update :victims conj victim)))

(send james-bond murder "Dr. No")

james-bond

(send james-bond murder "Goldfinger")

;; We'll see later, with threads, how this can be used.
;; One interesting property: SENDs are held off until the
;; end of a txn, so

;; Also send-off.


(comment
 (dosync
  (alter foo hack-hack)
  (alter bar frobnicate)
  ;; This will be safe!  We will get only 1 log message.
  (send log-agent log "I managed to alter both foo and bar.")))



;;;;  Threading constructs.

;;; Futures

;; - starts a new thread
;; - doesn't block code in which future is called

(defn do-some-work []
  (Thread/sleep 3000)
  (println "Word done on: "(.getName (Thread/currentThread)))
  (rand-int 100))

(defn some-busy-fn []
  (println "I do some stuff")
  (future (do-some-work))
  (println "I did some stuff"))

#_
(some-busy-fn)

;;; BTW - how do we inspect java objects?  Show the inspector,
;; and how to get to the list of methods on the Thread class


(defn another-busy-fn []
  (let [result (future (do-some-work))]
   (println "I do some stuff")
   ;; Now I need the result:
   (println "Our work yielded: " @result)
   (println "All done."))) ; now we block!

(another-busy-fn)

;; Note -- @foo is just a macro which expands into (deref foo)
;; But deref takes some interesting extra arguments:
(defn an-impatient-fn []
  (let [result (future (do (do-some-work)
                           (println "I did the work anyway")))]
   (println "I do some stuff")
   ;; Now I need the result:
   (println "Our work yielded: " (deref result 1000 :too-late))
   (println "All done.")))

(an-impatient-fn)


;;; A note on dynamic vars
(def ^:dynamic *foo* 42)

(def d2
  (binding [*foo* 69]
    (future
      (println "In the future:")
      (println "We have " *foo*))))


;;;; Delays

;; Sort of like futures, but might never get executed

;; The example in the book is very neat, make sure you understand it.

;; Another common use is to do something like:

(comment
  (def some-resource
    ;; We can't get the value of some-resource when we load the file,
    ;; before the program has started.  Maybe it needs to wait until
    ;; main has connected to a database or something.
    (delay (expensive-operation-to-acquire-resource-at-runtime)))


  (defn hairy-function []
    ;; .... code code
    ;; Force evaluation of some-resource; only runs first time.
    (foobar @some-resource)))


;;;; Promises

;; Can act sort of like a synchronization point.

;; Easy to get your knickers in a twist with promises, so be careful!

;; We will see better ways of doing this sort of thing later with core.asyn CHANNELS.

(def p (promise))

(future
  (println "I'm waiting for it!")
  (println "I got: " @p))

(deliver p "Rhythm")

;; Second time, nothing happens, our future still got rhythm
(deliver p "nuthin")


;;;; Other mechanisms:

;; Plain java thread:

(def java-thread
  (java.lang.Thread. #(println "Hello from " (java.lang.Thread/currentThread)) ))

(.start java-thread)

;; Note -- above! won't convey binding of specials!

;; Also, clojure.core.async, but we'll cover that later.

;; If I have time:  Live  code some threads which watch
;; agents, refs etc.
