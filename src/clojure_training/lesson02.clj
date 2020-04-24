(ns clojure-training.lesson02)

;;;; We will fill this in in a live setting fashion.


;;; Question about syntax of anonymous functions

(def foo
 (fn [x y] (+ x y))
  )

(let [y 5]
  (map (fn my-foo [a b] (+ a b y))
       (range 5)
       (range 5)))

(fn [a b] (+ a b ))
(fn my-foo [a b] (+ a b ))

(comment
  ;; The following two forms are *exactly* the same.
  ;; There is no performance penalty to the first firm.
  (-> foo
      (bar x)
      (quux y))

  (quux (bar foo x) y))

(let [y 5]
  (map #(+ %1 %2 y)
       [1 2 3]
       [99 100 105]))


;;;;;;;
;;
;; - [X] how to get help
;; - [X] navigate sources
;; - [X] structural editing
;; - [X] using maps
;; - [X] gotchas with =conj=
;;
;;;; Any questions on any of that?
;;
;; This lesson goals are in ~/Consulting/clients/gojee/work/clojure-training/doc/lesson-02.org
;;  * Review LET and lexical scope.
;;  * lexical vs dynamic scoping
;;  * destructuring
;;  * sequences, lazy seqs
;;  * reduce, functional idioms
;;


;;;;  * Review LET and lexical scope.

;; Remember we wrote this
  (let [x 7
        y 3]
    ;; ... in here
    ;; x and y exist.
    (+ x y))

;; What if we write
(let [x 4]
  (let [x 7] ; shadowing
    (str "X is now: " x))
  (str "X is finally " x))

(let [x 1] ; <= binding
  ;; here is the SCOPE
  (str "X is now: " x))

;; we say that the BINDING of x has been SHADOWED.
;; The value of X is the one in LEXICAL scope.
(defn show [x]
  (println "In SHOW: the value of X is:" x))

;; What is printed by the following?
(let [x 7]
  (show x)
  (show 3))

;; The argument vector of a function (here [x]) acts as a
;; lexical scoping construct.
;; Other constructs which introduce lexical scope include
;; loop, for, doseq, dotimes, while and many more.



;;;; Closures (not clojures)
;; When we did this last time:

(def blah
 (let [x 3]
   (fn [y]
     ;; We say that the returned function CLOSES OVER the variable x.
     (+ x y))))

(blah 6) ; no scope for x here!

;; A function which "remembers" what was in lexical scope is called a CLOSURE.
;; clojure is a play on word of "closuRe" on "Java".  So now you know.  ;-)




;;;; Dynamic scope.

;; There also exists something called DYNAMIC scope.
;; where variables take their value from the nearest
;; binding on the call stack.
;; emacs lisp, for example, uses dynamic scope for all variables.
;; The first lisp to introduce lexical scope was SCHEME.
;;


(def ^:dynamic *bigvar* 42)
(def regular 42)

(defn blah []
  (show *bigvar*))

(defn blah-regular []
 (show regular))

(blah-regular)
;; Gets it's value from the nearest frame on the stack,
;; in this case, the global environment.


(binding [*bigvar* 99]
  ;; This is called SHADOWING the binding.
  (binding [*bigvar* 7]
    (blah)))

(let [regular 5555]
  (blah-regular))


;;; Dynamic bindings can act as thread-local "global" variables.
;;; But they are frowned upon in clojure, as they break REFERENTIAL TRANSPARENCY.

;; referential transparency: the property that a pure function, given
;; some arguments, will ALWAYS return the same value!

;;; Clojure provides some dynamic variables out of the box, e.g. *out*
*out*
*in*

;; Can guess why this is useful?

(comment ; Important example of an oft-used dynamic var
  (binding [*out* (some-log-file-stream)]
    (-main)))

;; We won't worry too much about dynamic vars until we do multithreading stuff.



;;;;  * destructuring (chapters 3, 5)

(let [x [1 2 3]]
  (first x)
  (rest x ))

(let [[a b c] [1 2 3]]
  {:a a
   :b b
   :c c})


(let [[a & more] [1 2 3]]
  ;; Common idiom for recursing; grab start of the seq,
  ;; and recurse on MORE
  {:a a
   :more more})

(let [[a & more] []]
  {:a a
   :more more})

;;; Destructuring maps --- memorize this!!

(def grocery-entry
  {:liquid "Milk"
   :colour "white"
   :price  299})


(let [{l :liquid c :colour} grocery-entry]
  [l c])

(let [{l :liquid c :color u :unknown} grocery-entry] ; Notice the typo!!
  ;; Source of many a hard to track bug.  The perils of dynamic typing.
  [l c u])


;;; Another, very common way of destructuring a map:
grocery-entry

(let [colour (get grocery-entry :liquid)
      liquid (get grocery-entry :colour)]
  ;; scope.
  [liquid colour ])

(let [[& more] {:status 200 :method "GET" :query-string "foo=7&bar=9"}]
  ;; scope.
  more)

;; Where can you find out all this crazy stuff?
;; You must (eventually) read the reference, all the way through,
;; at least once.  Destructuring is covered here:
;; https://clojure.org/reference/special_forms#binding-forms

(let [[first-name last-name :as full-name] ["Stephen" "Hawking"]]
      full-name)

(let [{:keys [liquid] :as m} grocery-entry]
  (show liquid)
  (assoc m :foobar true))

(defn blah [{:keys [liquid] :as full-map}]
  (show liquid)
  (assoc full-map :foobar true))

(blah grocery-entry)


(let [{l :liquid c :color u :unknown} grocery-entry] ; Notice the typo!!
  ;; Source of many a hard to track bug.  The perils of dynamic typing.
  [l c u])


;;

;;; Anywhere that clojure lets you bind variables, it lets you destructure,
;;; which is very convenient.


;;;;  * sequences, lazy seqs

;; Last time we noticed:
(rest [1 2 3]) ; not a vector ?!

;; Basic structure of the LOOP macro.
(loop [x [1 2 3]]
  (println [x (class x)])
  (if (seq (rest x))
    (recur (rest x))))

;; As above, but more idiomatic:
(loop [[x & more] [1 2 3]]
  (println x)
  (if (seq more) ; In common lisp, the (seq ..) part would be unneccessary.  This is called NIL PUNNING.
    (recur more)))

;; clojure tries to encourage LAZINESS.  e.g.

(def all-integers (range)) ; safe to evaluate!

;; But don't try to print it!

;; What can we do with it then?

(take 10 all-integers)


(->> all-integers ; The "thread last" macro
     (drop 9999)
     (take 10))


;; ->  threads into the FIRST position
;; ->> threads into the LAST position

(-> grocery-entry
    (assoc :on-special? true)
    ;;    ^ here is where grocery-entry gets spliced in
    (dissoc :liquid)
    ;;    ^ here is where result of above computation gets spliced in
    )

(->> all-integers ; The "thread last" macro
     (drop 77)
     ;;        ^ here is where all-integers gets spliced
     (take 10)
     ;;      ^ here is where result of the drop 10 gets spliced
     (map inc)
     )


;; Functions which create and return lazy sequences:

repeat repeatedly
take
drop
iterate

(take 5 (repeat :foo))

(zipmap [:a :b :c]
        (range))

(class (range 5 9)) ; Both just SEQS.
(class (range))


;;; Laziness gotchas

(declare process-file)

(defn -main [& files]
  (map process-file files))

;; And then calling
;; lein run
;; on command line seems to do nothing to files.  Why?

(defn -main [& files]
  (doall (map process-file files))
  ;; or, if you don't want the resulting sequence in memory
  (doseq [file files]
    (process-file file)))

;;;;;


;;;;  * reduce, functional idioms

;; Remember this problem?  Histogram of letters?  :-)

(loop [[ch & str] "hello"
       hist {}]
  (if ch
    ;; more characters?
    (recur str
           ;; Modify the map each time through the loop
           (assoc hist
                  ch
                  (+ 1 (get hist ch 0))))
    hist))



;; Do without fnil, explain error, explain fnil, fix function

((fnil (fn [x]
         (str "We got given: " x))
       :some-default)
 nil)

(update {}
        :counter
        (fnil inc 0))


(reductions (fn [hist letter]
          (update hist letter (fnil inc 0)))
        {}
        "hello")

(defn count-letters [word]
 (reduce (fn [hist letter]
           (update hist letter (fnil inc 0)))
         {}
         word))

(frequencies "hello")

(defn print-hist-from-stream [stream]
  (println (count-letters (read-line stream))))

(count-letters "this is a long word")


;;;;
;;
;; http://www.4clojure.com/problem/53
;; Longest subsequence
