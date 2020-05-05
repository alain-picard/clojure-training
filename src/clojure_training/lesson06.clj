(ns clojure-training.lesson06)


;;;; Metadata

(meta #'clojure.core/+)
;; =>
;; {:added "1.2",
;;  :ns #namespace[clojure.core],
;;  :name +,
;;  :file "clojure/core.clj",
;;  :inline-arities #function[clojure.core/>1?],
;;  :column 1,
;;  :line 984,
;;  :arglists ([] [x] [x y] [x y & more]),
;;  :doc
;;  "Returns the sum of nums. (+) returns 0. Does not auto-promote\n  longs, will throw on overflow. See also: +'",
;;  :inline #function[clojure.core/nary-inline/fn--5526]}

;; Where the heck did all this stuff come from?  What's it for?

(defn  ^Integer foo-bar
  "Does some foo thang to them bar doohickies"
  {:inventor "Doc Brown"}
  [x y]
  (Integer. (+ x y 2)))

(meta #'foo-bar )

{
 ;; Arglists got placed in by the defn macro, which analyzed
 ;; the structure of the function we defined.
  :arglists ([x y]),

 ;; :doc got placed in my our docstring.
 :doc "Does some foo thang to them bar doohickies",

 ;; Tag came from our type hint.
 :tag java.lang.Integer

 ;; This one is the one we placed manually.
 :inventor "Doc Brown"

 ;; These 3 got registered by the compiler, so that tooling
 ;; can take advantage of it (That's how M-. in emacs works, for example)
 :line 22,
 :column 1,
 :file "/home/ap/Consulting/clients/gojee/work/clojure-training/src/clojure_training/lesson06.clj",

 ;; The name and namespace in which the var is found
 :name foo-bar,
 ;; :ns #namespace[clojure-training.lesson06]
 }

;;; Some macros are annoying and incorrectly defined, e.g.

(def foo-1 "This is the first foo" 42)

(defonce foo-2 99)                      ; This macro has no place for a docstring.
                                        ; How do we document it?

;; We resort to this hackishness, by manually adding
;; the :doc key in the var's meta:
#_
(alter-meta! #'foo-2 assoc :doc "The second foo is important, too!")


;; Other common metadata tags:

;; ^:dynamic   -- declare dynamic vars
;; ^SomeType   -- type hint -- like ^{:tag java.lang.String}
