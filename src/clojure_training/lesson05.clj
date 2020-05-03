(ns clojure-training.lesson05
  (:require
   [clojure.pprint :as pprint]
   [clojure.java.io :as io]))

;;; The READER.

;; Lisp is strange; it has an extra step that other language don't have: the READER.
;;
;; Other languages:
;; (source code text)  =>  (compiler)         => (machine executable code)
;;  characters            intermediate AST         Binary code
;; Lisps:
;; (source code text)  =>  (reader)     => (compiler)     => (machine executable code)
;;  characters         =>  lisp objects => lisp objects   =>  Binary code
;;
;; The READER function is what does MACROEXPANSION.
;; The compiler receives LISP STRUCTURES as input.
(macroexpand-1 '(-> foo (bar) (baz)))
;; => (baz (bar foo))
;; When then reader loads your file, and sees (-> foo (bar) (baz)),
;; it sends (baz (bar foo)) to the compiler.
;; That's why there is no performance penalty to using one form over another:
;; by the time they get turned into machine code, the inputs are the SAME.

;;;; READ syntax.
;;
;; You may have noticed a bunch of strange characters, occasionaly

#_ (this form is never seen by the compiler)

;; Inspect these:
#inst "1985-04-12T23:20:50.52Z"
#uuid "f81d4fae-7dec-11d0-a765-00a0c91e6bf6"

;;;; Java Interop

;; Constructors:

(new java.util.Date)

;; Or, more usually, and succinctly, with trailing dot:
(java.util.Date.)


;; dot (.) macro

;; Instance methods:

(.toUpperCase "FooBar")


(macroexpand '(.toUpperCase "FooBar"))
(. "FooBar" toUpperCase)

;; Another example:
(.getYear (java.util.Date.))


;;; dotdot (..) macro

;; Remember this?
(. "FooBar" toUpperCase)

#_ ; Won't work
(. "FooBar" toUpperCase toLowerCase)

;; But this neat workaround exists:
(.. "FooBar" toUpperCase toLowerCase)

(macroexpand '(.. "FooBar" toUpperCase toLowerCase))
;; => (. (. "FooBar" toUpperCase) toLowerCase)

;; So .. acts as
;; sort of a "thread first for Java interop" idea.
;; Fails to work if you need to pass arguments to the methods

(macroexpand '(.. "FooBar" toUpperCase (toLowerCase "blah")))

YOU WERE HERE:
I don't fully understand difference between .. and doto.

(let [x (java.util.Date.)]
  (. x (setYear 159))
  x
  )
(.. (java.util.Date.) (setYear 159))


;;; doto macro

(doto (java.util.Date.)                 ; Even MORE fuckin' insane... :-(
  (.setYear 137)
  (.setMonth 0)
  (.setDate 1)) ; macroexpand this


;;; Static methods:

(.version (java.util.UUID/randomUUID))


;;; memfn

;;; proxy

;;; reify

reify
deftype
definterface

extend

(deftype FooBar [name age])
(def bob (FooBar. "Bob" 57))

(.name bob)
(->FooBar "bob" 55)


;;; gen-class


;;;; Show better time management libraries
;; clj-time,



;;;; I/O

(def path "/tmp/mystuff")

(spit "/tmp/mystuff" "This clojure class is too hard!\n")

;; There's some options we can give;
(spit "/tmp/mystuff" "This clojure class not so bad after all.\n" :append true)

(slurp "/tmp/mystuff")


;; Readers
(with-open [rdr (io/reader path)]
  (line-seq rdr)) ; Why does this blow up?

(with-open [rdr (io/reader path)]
  (into [] (line-seq rdr)))

(with-open [rdr (io/reader path)]
  (binding [*in* rdr]
    (read-line)))

(with-open [rdr (io/reader path)]
  (read rdr))

(io/make-input-stream path)


read-string

;; The writer
pr prn
clojure.pprint/pprint
clojure.pprint/cl-format ; hardcore!  See the Hyperspec.

with-open

io/reader
io/writer

io/resource


with-out-str  with-in-str


ns 
import
