(ns clojure-training.lesson06
  (:require [medley.core :as m]
            [cheshire.core :as json]
            [superstring.core :as s]
            [camel-snake-kebab.core :as csk]
            [clj-http.client :as http]
            [slingshot.slingshot :refer [try+ throw+]]
            [clojure.java.io :as io]))



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
  "Do some foo thang to them bar doohickies"
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


;;;; Exceptions; try/catch, slingshot

;; The Java exception hierarchy:
;; Show picture in ../../resources/java-exceptions.png
;;
;; EXCEPTION:  classic one is NullPointerException
;; e.g.
#_
(+ 1 nil)

;; RuntimeException ISA Exception
;; so called "unchecked" exceptions; may be thrown even if not in
;; the java method signature.  For clojure, this difference is not germane.

;; Another example:
#_
(/ 1 0)

;; And another
#_
(nth [1 2 3] 9)

;; ERROR:  classic one is StackOverflowError:

(defn foobar []
  (foobar))

#_
(foobar)

;; Catching Throwable?  hum...
;; That includes things like AssertionError, OutOfMemoryError ...
;; You program is most likely hosed.
;; But NullPointerException /MAY/ also indicate a logic bug... so...

;; In practice, it can get hard to decide what to handle and where.
;; The rule to try to follow is this:
;;
;; - Throw an Error  (or one of its subclasses) if you cannot continue.
;;   You'll probably never want to do this in clojure.
;;
;; - Throw an Exception (or one of its subclasses) if you CAN continue.
;;   This is the normal case.
;;
;; - Catch Exception where it makes sense for the program to handle it
;;   and continue in a sensible way.
;;
;; - Don't catch Error, except maybe to log and kill (or restart) the application.
;;

;;; That all being said:
;;;    The mechanism:

(defn bad-math [] (/ 1 0)) ; This isn't going to end well.  :-(

;; We can try to catch everything:
(try (bad-math)
  (catch Exception e
    (println "Oof!  We caught an: " e)
    ;; Do some better math!
    42))

;; We can try to be specific:
(try (bad-math)
  ;; They get attempted in the order shown.  Demonstrate.
  (catch java.lang.ArithmeticException e
    (println "Oof!  Back to school for remedial math lessons: " (.getMessage e))
    42)
  (catch Exception e
    (println "Oof!  We don't know what happened: " e)
    nil))

;; Sometimes, we don't want to catch the exception, but need to do some
;; cleanup :

(let [resource (atom {:some-resource :open})]
  (def grabbed-resource-for-didactic-purposes resource) ; Don't do this!!
  (try (bad-math)
    ;; They get attempted in the order shown.  Demonstrate.
    (finally
      (swap! resource assoc :some-resource :closed))))

;; This is more or less what the with-open macro does
(with-open [f (io/reader "/tmp/foo")]
  (bad-math)) ; Macroexpand this


;;; We can throw our own exceptions:

(throw (Exception. "Woops!"))

;; As discussed, in clojure, it's a bit blurry what we want to
;; catch and throw; so most of the time, if you "don't really care",
;; you can use this functions:

(instance? RuntimeException
 (ex-info "Some bad situation" {:integer 42}))
;; This throws a clojure.lang.ExceptionInfo, which is a RuntimeException.

(try (throw (ex-info "Some bad situation" {:integer 42}))
  (catch clojure.lang.ExceptionInfo e
    (println "Data: "(ex-data e))
    (println "Message: " (ex-message e))))


;;; A better exception library: SLINGSHOT

;; Catching/throwing ex-info is... bare bones and limiting.
;; slingshot extends this idea

;; This is straight from their documentation at: https://github.com/scgilardi/slingshot
(declare bad-tree? parse-good-tree parse-tree)

(defn parse-tree [tree hint]
  (if (bad-tree? tree)
    ;; We can throw any object; most usually a map:
    (throw+ {:type ::bad-tree :tree tree :hint hint})
    (parse-good-tree tree hint)))

;; And then catch based on properties of this map:
(defn read-file [file tree]
  (try+
    [...]
    (tensor.parse/parse-tree tree)
    [...]
    (catch [:type ::bad-tree] {:keys [tree hint]}
      ;; If we hit a bad tree we can destructure what was thrown
      (log/error "failed to parse tensor" tree "with hint" hint)
      ;; and rethrow if we need to.
      (throw+))
    (catch Object _
      (log/error (:throwable &throw-context) "unexpected error")
      (throw+))))

;; We will see a live example below in an http request.


;;;; Strings, regexes

;; We can start with the 21 utilities in clojure.string:
;; ------------------------------------------------------
;;
;; reverse                   triml
;; re-quote-replacement      trimr
;; replace                   trim-newline
;; replace-first             blank?
;; join                      escape
;; capitalize                index-of
;; upper-case                last-index-of
;; lower-case                starts-with?
;; split                     ends-with?
;; split-lines               includes?
;; trim
;;
;; This shows us that reading the source is always useful.
;;

;;;; Libraries
;;
;;
;; Another nice library is `superstring`
;; (:require [superstring "3.0.0"])
;; Has nice property that it just re-aliases all of clojure.string,
;;
;; Contains such cool utilities as:
;;    longest-common-substring
;;    distance  (levenshtein distance)
;;    chomp, chop (like Perl)
;;    capitalize
;;    camel-case, lisp-case etc (replacing camel-snake-kebab, which we'll see later)
;;    trim, pad-left and right, etc.

(s/longest-common-substring "abcdefg"  "23abc55cdef")
(s/distance "foobar"  "fo0bar")
(s/distance "foobar"  "f00bar")

(s/chomp "foo\n")
(s/trim " some string ")
(s/pad-left "some string" 20)


;; Another interesting one is "cuerdas"  (Spanish for rope, or string, I think?)

;; I like both of these libs because they are cross compatible with clojurescript.
;;

;;; Medley
;; Docs at: https://weavejester.github.io/medley/medley.core.html

(m/abs -3) ; Really?  This doesn't exist in standard clojure??

(let [foo (atom 17)]
  [(m/deref-swap! foo + 3) @foo])       ; Useful, AND safe!

(-> {:foo {:bar 7 :blah 9}}
    (m/dissoc-in [:foo :bar]))

;; Here's one everyone ends up writing eventually:
(m/map-vals inc
            {:foo 9
             :bar 42
             :blah 69})

(m/update-existing {:foo 9
                    :bar 42
                    :blah 69}
                   :bar
                   inc)

(m/update-existing {:foo 9
                    :bar 42
                    :blah 69}
                   :missing
                   inc) ; Doesn't raise an error.


;; For more very, very hairy and powerful operations on nested structures,
;; go look at the Spectre library.  Scary stuff!


;; And tons of other useful things.  Go have a look!



;;; camel-snake-kebab

(csk/->kebab-case-keyword "FooBar")

(csk/->camelCaseString :some-resource) ; etc.


;;; clj-http  -- an HTTP client
;;
;; curl https://api.stripe.com/v1/charges -u sk_test_4eC39HqLyjWDarjtT1zdp7dc:
(def response
  (delay
   (http/get "https://api.stripe.com/v1/charges"
             {:basic-auth "sk_test_4eC39HqLyjWDarjtT1zdp7dc:"})))

;; We can't use slurp because we need to pass HTTP options...

#_
(print (:body response))

;;; Cheschire  --- a fast JSON encoder/decoder

#_
(json/parse-string (:body response))


;; And look how well these can play together:
#_
(json/parse-string (:body response) csk/->kebab-case-keyword)


;; clj-http collaborates with slingshot (not many libraries do, unfortunately)

(try+ (http/get "https://api.stripe.com/v1/charges" {:throw-entire-message? true})
  ;; Status 401 is request unauthorized
  (catch [:status 401] {:keys [request-time headers body]}
    [401 request-time headers]
    headers)) ; and then body


;;;; Multi methods

;; Multiple dispatch, Object orientation "a la carte"

(ns-unmap *ns* 'speak)                  ; What to do when you get in trouble

(defmulti speak :type)

(defmethod speak :dog [animal]
  (str (:name animal) " says woof woof!"))

(speak {:name "Fido" :type :dog})

(speak {:name "Sylvester" :type :cat})

(defmethod speak :default [animal]
  (str (:name animal) " can't speak; don't be silly!"))

(defmethod speak :cat [animal]
  (str (:name animal) " says miao!"))

;; This mimics standard, type-based single dispatch,
;; with extension that we can define any predicate we like
;; to decide "what kind" of thing is being dispatched on.
;;
;; But we can do more than this:  multiple dispatch!

;; An interesting example: State machines

;; Consider a vending machine, which accepts coins and
;; drops a soda

(def vending-machine {:state :start :coins []})

(def price-of-soda 220)

(defn coin-value [machine] (reduce + (:coins machine)))

(ns-unmap *ns* 'handle)

(defmulti handle (fn [machine event] [(:state machine) (:type event)]))

(defmethod handle [:start :coin] [machine event]
  (println "Getting a coin in the start state: " (:coin-value event) " cents.")
  (-> machine
      (assoc :state :accumulating-money)
      (update :coins conj (:coin-value event))))

(-> vending-machine
    (handle {:type :coin :coin-value 20}))

(defmethod handle [:accumulating-money :refund] [machine event]
  (println "Getting a coin in the start state: " event)
  (println "Refunding " (:coins machine))
  vending-machine)


;; Buyer changes his mind
(-> vending-machine
    (handle {:type :coin :coin-value 20})
    (handle {:type :refund}))

;; Buyer really wants that soda
(-> vending-machine
    (handle {:type :coin :coin-value 20})
    (handle {:type :coin :coin-value 200}))

;; Oops!  Let's implement it:
(defmethod handle [:accumulating-money :coin] [machine event]
  (println "Getting a coin in the accumulating state: " event)
  (let [machine (-> machine
                    (assoc :state :accumulating-money)
                    (update :coins conj (:coin-value event)))]
    (if (< (coin-value machine) price-of-soda)
      machine
      (do                               ; We have enough money!
        (println "Disbursing delicious soda.")
        vending-machine))))

(-> vending-machine
    (handle {:type :coin :coin-value 20})
    (handle {:type :coin :coin-value 200}))

;;; Assignment: Extend this machine so it can make proper change
;;; if user pays too much.

;; A common pattern with state machines:

(let [events [{:type :coin :coin-value 20}
              {:type :coin :coin-value 20}
              {:type :coin :coin-value 200}
              {:type :coin :coin-value 200}
              {:type :coin :coin-value 200}
              {:type :coin :coin-value 10}]]
  ;; Now you can imagine events being an infinite lazy stream...
  ;; and this can be the main of the entire program.
  (reduce handle
          vending-machine
          events))


;;;; Finding good libraries

;; Unfortunately hard; lots of libs, which ones are any good?

;; https://www.clojure-toolbox.com
;;
;; - Many of the libs under https://github.com/clj-commons, such as
;;   - useful (more utils, like Medley)
;;   - fs Sane access to the file system; mainly Java wrappers
;;   - ring-buffer A oft-used structure


;;;; Docstrings

;; Some examples so far:
(def search-engines {:bing "https://www.bing.com/search?q="
                     :yahoo "https://au.search.yahoo.com/search?p="
                     :yippy "https://www.yippy.com/search?query="})

(defn search
  "return the first page of result from search engines"
  [text]
  (let [result (promise)]
    (doseq [search-engine (vals search-engines)]
      (future (if-let [results-page (slurp (str search-engine text))]
                (deliver result results-page))))
    @result))

;; c.f.

(def search-engines
  "The online search engines available to the FOOBAR software.

  The value should be a map, with values consisting of the string
  prefix required to form a valid URL to be sent to the engine."
  {:bing "https://www.bing.com/search?q="
   :yahoo "https://au.search.yahoo.com/search?p="
   :yippy "https://www.yippy.com/search?query="})

(defn search
  "Return the search results of text.

  Each engine in `search-engines` will be searched, and the results
  of the first one to respond will be returned."
  [text]
  ;; elided
)

;; Note how we can cross-ref other functions and vars?
