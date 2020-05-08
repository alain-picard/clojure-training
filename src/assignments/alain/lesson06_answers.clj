(ns assignments.alain.lesson06-answers
  (:require [clojure.java.io :as io]))


;;;;  Comments on assignments


;; I got sent this:
(defn format-regex
  "Returns an executable regex from REGEX"
  [regex]
  (read-string (str "#\"" regex "\"")))

;; First comment is; this function exists:
(re-pattern ".*Clojure.*")


;; But, more importantly, be wary of calling functions like READ-XXX
;; as they are very slow, and usually not necessary.
;; See if a constructor for they type you want to produce already exists.
;; e.g.

(java.util.regex.Pattern/compile ".*Clojure.*")


;;; Misconception about assert.

(defn lazy-grep
  "Returns a lazy sequence of matches of a regex pattern from a file"
  [filepath regex]
  (assert (.exists (io/file filepath)))
  ;;
  ;; Body of function deleted...
  )

;;;;  Alain's solutions

(defn grep
  "A unix-grep lookalike.  Return a vector of each line in FILE
  which contains one or more matches of the supplied REGEX-STRING."
  [file regex-string]
  (let [re (re-pattern regex-string)]
    (with-open [rdr (io/reader file)]
      (doall
       (filter (partial re-find re) (line-seq rdr))))))

#_
(grep "/tmp/mystuff" "no.")

(defn lazy-grep  [file regex-string]
  "A unix-grep lookalike.  Return a lazy seq of each line in FILE
  which contains one or more matches of the supplied REGEX-STRING.

  And... problem?"
  (let [rdr (io/reader file)]
    (letfn [(match-line? [line] (re-find (re-pattern regex-string) line))
            (proceed []
              (let [line (.readLine rdr)]
                (cond
                  (nil? line)        (do (.close rdr) nil)
                  (match-line? line) (cons line (lazy-seq (proceed)))
                  :else              (lazy-seq (proceed)))))]
      (proceed))))

(def some-lines (lazy-grep "/tmp/test.in" "WARN"))

(take 10 some-lines)

#_
(take 10 (lazy-grep "/tmp/test.in" "WARN"))

;; What is the problem with the above code?

;; What use is this code, really?  It allows you to do this:

(defn foobar [file]
  ;; This will happily run on a terabyte long file.
  (doseq [line (lazy-grep file #"WARNING")]
    (email-sysadmin line)))


;; Implementing one in terms of the other: trivial!
(defn grep2 [file regex-string]
  (doall (lazy-grep file regex-string)))

(->> some-lazy-seq
     (map foo)
     (map bar)
     (filter blah))


;;;;;;;;;;;
