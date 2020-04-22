;; We need this form, I'll explain why in a later class.
(ns clojure-training.lesson01)


;; This is a comment

;;;; This is also a comment.  Typically to indicate a new section.
;; We'll talk about coding style and conventions at some point.

(comment
  (this-is a commented out function call))


;;;;  Syntax

;; Not:
;;  1 + 2; // but
(+ 1 2)

;; Lisp is verbose?  Lots of stupid parentheses?

;; vs 1 + 2 + 3 + 4 + 5;
(+ 1
   (+ 2
      (+ 3
         (+ 4 5))))

;; How about
(+ 1 2 3 4 5)

;;   (   operator    arg1  ... argN  )

;; operators are almost, but not always, FUNCTIONS.


;;;; Fundamental types

"hello"

7

7.0

7/3  ; exact ratios
(* 7/3 3)
(* (/ 7.0 3.0) 3.0)


7N

java.lang.Long/MAX_VALUE

#_
(+ 9223372036854775807 1) ; ouch!

(+ 9223372036854775807N 1) ; OK!

;; The REPL
;; --->   Read Eval Print Loop
(comment ; a conceptual implementation
 (loop
   (print
    (eval
     (read)))))


:foo-bar   ; I am a keyword
(comment
  some-symbol) ; I am a symbol

{:liquid "Milk"
 :colour "white"
 :price  299}

[1 2 3] ; I am a vector

[1 :banana "split"]

;; Sets:
(=  #{1 3 2}
    #{3 1 2})

;; CONJ

(conj #{1 3 2} 99)

(into #{} (range 99))

(conj [1 2 3]  99)

(conj {:foo 99}  [:blah 77])

(conj (list 1 2 3) 0)

#{1 2 3} ; I am a set order is immaterial


;;; VALUES.

;;; State: using DEF for VARS

(def foo 7)

(def grocery-entry {:liquid "Milk"
                    :colour "white"
                    :price  299})

;; ASSOC

(assoc grocery-entry
       :on-special?  true)

(dissoc grocery-entry :price)

(update grocery-entry :price inc)
(update grocery-entry :price - 100)

(defn put-on-special [entry discount]
  ;; mention that item is on special
  ;; ajust price to new discount
  (dissoc
   (update entry
    (assoc entry :on-special? true)
                  :price * discount)
   :liquid))

(defn put-on-special [entry discount]
  ;; mention that item is on special
  ;; ajust price to new discount
  (-> entry
      (assoc :on-special? true)
      (update :price * discount)
      (dissoc :liquid)))

(put-on-special grocery-entry 0.8)

;;;; FUNCTIONAL PROGRAMMING

+                                       ; These are 1st class values
;; Defining new functions: the FN operator

(fn [x] (* x x))

;; We can give them names:

(def square (fn [x] (* x x)))

;; This is so common there is a shortcut for this: DEFN

(defn square [x] (* x x)) ; same as above


;; Functional programming is about using functions as first class
;; objects in your program; that is both as arguments AND return
;; values to functions

(map square [1 2 3 4 5])

;; map is called a "higher order function", because one or more
;; of it's arguments are themselves functions.

;; Clojure is chock-full of "higher order functions"
;; can you name some?

(filter odd? [1 2 3 4 5])

;;; More higher order functions: function composition
;;; From basic algebra:
;;  Given two functions f(x) and g(x), we can define
;;  h(x) === f⎄g  === f(g(x))

(defn f [x] (+ 1 x))
(defn g [x] (* 2 x))

(def h (comp f g))

(map g [1 2 3])

(def my-even (complement odd?))

(filter my-even [1 2 3 4 5]) ; behaves like even?

(filter even? [1 2 3 4 5])

(clojure.repl/doc filter)

;; practice:
;;  - calling up doc strings of everything you type
;;  - jumping to function definitions
;;  - structural editing


;; Our fist special operator: or special form: LET

(def example
  (let [x 7
        y 3]
;;; ... in here
    ;; x and y exist.
    (fn [in]
      (+ x y in))))


(def add-3
  (let [x 3]
    ; x is BOUND.
    (fn [y] (+ x y))))

(add-3 10)

;; Generalize this:

(defn make-n-adder [n]
  (let [x n]
    (fn [y] (+ x y))))

(make-n-adder 99)

((make-n-adder 99) 10 )

;; remember this problem?
;; (letter-frequency "hello")  =>  h 1 e 1 l 2 o 1

(first [1 2 3])
(rest [1 2 3])

(seq (rest []))
(if (rest [])
  'hello
  'nope)


;; Basic structure of the LOOP macro.
(loop [x [1 2 3]]
  (println x)
  (if (seq (rest x))
    (recur (rest x))))
