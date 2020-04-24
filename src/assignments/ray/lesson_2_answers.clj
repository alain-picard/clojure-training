(ns lesson-2-answers)

;;; Enter your answers in here.
(def words_1 ["meat" "mat" "team" "mate" "eat"])
(first words_1)
(rest words_1)
(def words_2 ["veer" "lake" "item" "kale" "mite" "ever"])

(defn toS [words]
  (set words))

(toS words_1)

;;;; Finding the frequency of words and group them together

(defn find-anagram_1 [words]
  (map val (group-by frequencies words)))

(find-anagram_1 words_1)

;;;; In progress, putting into a set of sets

(defn find-anagram_2 [words]
  (into #{} (map val (group-by frequencies words)))) ;; How do I make the the return value into a set? Putting set in front of map does not work

(find-anagram_2 words_1)

;;;; In progress, filtering out the entries that are less than 2

(defn find-anagram_3 [words]
  (into #{} (filter #(> (count %) 1) (map val (group-by frequencies words)))))

(find-anagram_3 words_1)
(find-anagram_3 words_2)
