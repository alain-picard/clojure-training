(ns clojure-training.live-lesson04)


;;;; Futures

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

