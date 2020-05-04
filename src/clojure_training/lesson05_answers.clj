(ns clojure-training.lesson05-answers
  (:require [clojure.string :as s]))


(defn bing [term]
  (slurp (str "https://www.bing.com?q=" term)))

(defn yippy [term]
  (slurp (str "https://www.yippy.com/search?query=" term)))

(defn yahoo [term]
  (slurp (str "https://au.search.yahoo.com/search?p=" term)))


;; Excercise 1.

(defn search-fast [term]
  (let [p (promise)]
    (doseq [engine [yippy yahoo bing]]
      (future (deliver p [engine (engine term)])))

    (let [[winner text] (deref p 10000 [:timeout "No results."])]
      ;; I just took the substring so as to not print out insanely long lines.
      [winner (subs text 0 (min 80 (.length text)))])))


;; Excercise 2.
(defn search-fast-2 [term & engines]
  (let [p (promise)]
    (doseq [engine engines]
      (future (deliver p [engine (engine term)])))
    (let [[winner text] (deref p 10000 [:timeout "No results."])]
      ;; I just took the substring so as to not print out insanely long lines.
      [winner (subs text 0 (min 80 (.length text)))])))

(search-fast-2 "Lisp" bing yahoo)

(search-fast-2
 (s/escape "common lisp" {\space "&nbsp;"})
 bing yahoo)


;;; Excercise 3.



(defn referenced-urls [s]
  ;; a (poor) attempt at extracting some of the useful
  ;; URLs embedded in string S.
  (mapv second
        (re-seq #"href=\"(http://[=?.:/\w]+)" s)))

(->> " foo href=\"https://blah.com\"  and href=\"http://foo.com\"  "
 (re-seq #"href=\"(https?://[=?.:/\w]+)" ))

(defn get-urls [term & engines]
  (into []
        (flatten
         (for [engine engines]
           (referenced-urls (engine term))))))


;;; Chap 10

(defn fetch-1 []
  (slurp "https://www.braveclojure.com/random-quote"))

(defn words-in-sentence [s]
  (count
   (filter #(not= "" %)
           (s/split s #"\s+"))))

(defn count-words [quotes]
  (reduce +
          (map words-in-sentence quotes)))

(count-words #{"Going to church doesn't make you a Christian any more than standing in a garage makes you a car.\n-- Billy Sunday \n"
               "Make things as simple as possible, but no simpler -- Einstein."})

(defn quote-count-words [n]
  (let [results (atom {:hits 0 :quotes #{}})
        finished? (promise)]
    (dotimes [i n]
      (future
        (let [quote (fetch-1)]
          (swap! results
                 #(-> %
                      (update :hits inc)
                      (update :quotes conj quote))))
        (when (= (:hits @results) n)
          (deliver finished? true))))
    (and @finished?
         [(count-words (:quotes @results))
          @results])))


;;; Chap 10, ex 3.

(def paladin (ref {:name "Aragorn"
                   :hits-taken 0
                   :danger-zone 4
                   :max-points 40
                   :hit-points 15}))

(def wizard (ref {:name "Gandalf"
                  :magic-potions #{:ecru :silver :vermilion}}))


;;; The primitive operations which manipulate immutable values

(defn sustain-hit [fighter hit]
  (-> fighter
      (update :hits-taken inc)
      (update :hit-points - hit)))

(defn quaff [fighter potion]
  (assoc fighter :hit-points (:max-points fighter)))

(defn critically-wounded? [fighter]
  (< (:hit-points fighter) 4))

(defn alive? [fighter]
  (pos? (:hit-points fighter)))


;; The stateful functions, which act on identities

(defn hit! [fighter]
  (alter fighter sustain-hit (rand-int 5)))

(defn assist! [fighter wizard]
  (let [[[potion] remaining] (split-at 1 (:magic-potions @wizard))]
    (alter wizard assoc :magic-potions remaining)
    (when potion
      (alter fighter quaff potion))))


(def reporter (agent nil))

(defn report [_ fighter wizard]
  (let [{:keys [hit-points name]} fighter]
    (println name " has " hit-points " hit points left; "
             (:name wizard) " still has " (:magic-potions wizard))))


(defn combat! [fighter wizard]
  (dosync
   (hit! fighter)
   (when (critically-wounded? @fighter)
     (assist! fighter wizard))
   (send reporter report @fighter @wizard)))

(defn play-game []
  (dotimes [i 4]
    ;; We can have as many threads as we want
    ;; playing this game simultaneously, safely.
    (future
      (while (alive? @paladin)
        (combat! paladin wizard )
        (Thread/sleep (rand-int 1000)))
      (println "Game over!  Your paladin is dead!"))))

#_
(play-game)
