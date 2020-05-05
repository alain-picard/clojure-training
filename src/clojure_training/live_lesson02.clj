(ns clojure-training.lesson02)

;; Is screen recording on?
;;
;; Last week we covered:
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
  (let [x 7]
    (str "X is now: " x)))

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

(let [x 3]
  (fn [y]
    ;; We say that the returned function CLOSES OVER the variable x.
    (+ x y)))

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

(defn blah []
 (show *bigvar*))

;; Gets it's value from the nearest frame on the stack,
;; in this case, the global environment.

(blah)

(binding [*bigvar* 99]
  ;; This is called SHADOWING the binding.
  (blah))

;;; Dynamic bindings can act as thread-local "global" variables.
;;; But they are frowned upon in clojure, as they break REFERENTIAL TRANSPARENCY.

;; referential transparency: the property that a pure function, given
;; some arguments, will ALWAYS return the same value!

;;; Clojure provides some dynamic variables out of the box, e.g. *out*
*out*  *in*

;; Can guess why this is useful?

;; We won't worry too much about dynamic vars until we do multithreading stuff.



;;;;  * destructuring (chapters 3, 5)

(let [x [1 2 3]]
  x)

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
(let [{:keys [liquid colour]} grocery-entry]
  [liquid colour])

;; Where can you find out all this crazy stuff?
;; You must (eventually) read the reference, all the way through,
;; at least once.  Destructuring is covered here:
;; https://clojure.org/reference/special_forms#binding-forms


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
     (drop 9999)
     ;;        ^ here is where all-integers gets spliced
     (take 10)
     ;;      ^ here is where result of the drop 10 gets spliced
     )

;; Functions which create and return lazy sequences:

repeat repeatedly
take
drop
iterate


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
#_
(reduce (fn [hist letter]
          (update hist letter inc #_ (fnil inc 0)))
        {}
        "hello")


(defn count-letters [word]
  (reduce (fn [hist letter]
            (update hist letter (fnil inc 0)))
          {}
          word))

#_
(count-letters "hello")

;; The cheating way
(frequencies "hello") ; let's look at it's definition!



;; Anagram problem:
;; http://www.4clojure.com/problem/77

(defn anagram? [a b]
  (= (sort a)
     (sort b)))

(anagram? "meat" "team")

(let [m {"team" #{"team"}
         "mite" #{"mite"}}
      w "ITEM"]
  ;; Find which key, if any, in M is an anagram of w,
  ;; and add it to its collection of current anagrams.
  ;; If it's not an anagram of any, add it to M as 1<=>1.
  (let [[match] (filter (partial anagram? w) (keys m) )]
    (if match
      (update m (first match) conj w)
      (assoc m w #{w}))))

;; Once we have the above, we can wrap it in a function
(defn build-anagrams [m w]
  ;; Find which key, if any, in M is an anagram of w,
  ;; and add it to its collection of current anagrams.
  ;; If it's not an anagram of any, add it to M as 1<=>1.
  (let [[match] (filter (partial anagram? w) (keys m) )]
    (println match)
    (if match
      (update m match conj w)
      (assoc m w #{w}))))


(reduce build-anagrams
        {}
        ["veer" "lake" "item" "kale" "mite" "ever"])
;; Seems to work, so, assemble in final form

(defn anagrams [words]
  (->> (reduce build-anagrams {} words)
       vals
       (filter #(> (count %) 1))
       set))

(anagrams ["meat" "mat" "team" "mate" "eat"])

(= (anagrams ["veer" "lake" "item" "kale" "mite" "ever"])
   #{#{"veer" "ever"} #{"lake" "kale"} #{"mite" "item"}})

(= (anagrams ["meat" "mat" "team" "mate" "eat"])
   #{#{"meat" "team" "mate"}})


;;;;
;; Another example -- powersets
;;
;; http://www.4clojure.com/problem/85

(declare all-subsets)

(defn power-set [x]
  (clojure.set/union #{}
                     #{x}
                     (all-subsets x)))

(defn all-subsets [s]
  (reduce (fn [acc x]
            (-> acc
                (conj #{x})
                (conj (disj s x))
                (clojure.set/union (all-subsets (disj s x)))))
          #{}
          s))

(all-subsets #{1 :a :Z})

(power-set #{1 :a})
(power-set #{1 2 3})


;;;;
;;
;; http://www.4clojure.com/problem/53
;; Longest subsequence

;; Given a vector of integers, find the longest consecutive
;; sub-sequence of increasing numbers. If two sub-sequences have the
;; same length, use the one that occurs first. An increasing
;; sub-sequence must have a length of 2 or greater to qualify.


;; The accumulator need not be an atomic value!
;; Here we need to keep track of two things
(let [[current best] [[1 2] [7 8 ]]
      item 3]
  (
   (fn [[c b] i]
     (if (<= item (last current))
       ;; end of sequence
       [[item] best]
       ;; we can keep going.  Have we beaten the best one yet?
       (let [current (conj current item)]
         (if (> (count current) (count best))
           [current current]
           [current best]))))
   [current best]
   item))

(defn longest-subsequence [coll]
  (second
   (reduce
    (fn [[current best] item]
      (if (and (seq current) (<= item (last current)))
        ;; end of sequence
        [[item] best]
        ;; we can keep going.  Have we beaten the best one yet?
        (let [current (conj current item)]
          (if (and (> (count current) 1) ; must be 2 or more entries to qualify
                   (> (count current) (count best)))
            [current current]
            [current best]))))
    [[] []] coll)))


(longest-subsequence [7 6 5 4])
(longest-subsequence [2 3 3 4 5])
(= (longest-subsequence [1 0 1 2 3 0 4 5]) [0 1 2 3])






;;;; Did anyone try the anagram puzzle?

;;;; Homework:
;; 
;; - Listen to the talk: [[https://www.infoq.com/presentations/Are-We-There-Yet-Rich-Hickey/][Are we there yet]]
;; - problems to try for next time:
;;   + implement a version of =filter= using a loop form, then using reduce.
;;   + implement a version of =update-in= 


:end
