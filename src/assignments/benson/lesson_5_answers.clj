(ns assignments.benson.lesson-5-answers)

(declare line-seq-custom re-seq-lines format-regex)

;; Text for easier testing
(comment
  "Clojure is a dynamic, general-purpose programming language, combining the 
   approachability and interactive development of a scripting language with an 
   efficient and robust infrastructure for multithreaded programming. 
   Clojure is a compiled language, yet remains completely dynamic – every 
   feature supported by Clojure is supported at runtime. Clojure provides 
   easy access to the Java frameworks, with optional type hints and type inference, 
   to ensure that calls to Java can avoid reflection. 
   
   Clojure is a dialect of Lisp, and shares with Lisp the code-as-data philosophy
   and a powerful macro system. Clojure is predominantly a functional programming
   language, and features a rich set of immutable, persistent data structures. 
   When mutable state is needed, Clojure offers a software transactional memory 
   system and reactive Agent system that ensure clean, correct, multithreaded designs.
   
   I hope you find Clojure's combination of facilities elegant, powerful, practical 
   and fun to use.
   
   Rich Hickey
   author of Clojure and CTO Cognitect")

(def path "src/assignments/benson/grep-text")

(defn grep
  "Reads a file from FILEPATH and returns a vector of sentences 
   that matches the REGEX pattern"
  [filepath regex]
  (with-open [rdr (clojure.java.io/reader filepath)]
    (re-seq-lines
     (into [] (line-seq rdr))
     (format-regex regex))))

(defn re-seq-lines
  "Returns a vector of sentences from TEXT which matches the REGEX pattern"
  [text regex]
  (remove nil?
          (map #(->> %
                     (re-seq regex)
                     (first))
               text)))

(defn format-regex
  "Returns an executable regex from REGEX"
  [regex]
  (read-string (str "#\"" regex "\"")))

 (grep path ".*Clojure.*") ; test with this



;; Failed attempt for reference
;; 
;; (defn lazy-grep
;;   ([filepath regex]
;;    (with-open [rdr (clojure.java.io/reader filepath)]
;;      (let [[line & other-lines] (into [] (line-seq rdr))
;;            parsed-regex (parse-regex regex)]
;;        (lazy-grep
;;         (first (re-seq parsed-regex line))
;;         other-lines
;;         parsed-regex))))
;;   ([n [line & other-lines] regex]
;;    (cons n
;;          (lazy-seq
;;           (lazy-grep
;;            (first (re-seq regex line))
;;            other-lines
;;            regex)))))


(defn line-seq-pattern
  "Returns the lines of text that succesfully match a pattern of REGEX
  from rdr as a lazy sequence of strings.
  rdr must implement java.io.BufferedReader."
  {:added "1.0"
   :static true}
  [^java.io.BufferedReader rdr
   regex]
  (when-let [line (.readLine rdr)]
    (if (re-seq regex line)
      (cons line (lazy-seq (line-seq-pattern rdr regex)))
      (lazy-seq (line-seq-pattern rdr regex)))))


;; This was the closest I could get to a lazy-seq because I couldn't
;; get it to work like (take 5 (lazy-grep ...)) due to an issue relating
;; to the BufferedReader stream closing
(defn lazy-grep
  "Returns N amount of matching sentences using a lazy sequence
   which will continue to search lazily until N sentences are found or
   the text file finishes"
  [filepath regex n]
  (with-open [rdr (clojure.java.io/reader filepath)]
    (doall (take n (line-seq-pattern rdr (format-regex regex))))))

(lazy-grep path ".*Clojure.*" 5) ; test with this