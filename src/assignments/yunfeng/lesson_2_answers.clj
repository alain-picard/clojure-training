(ns lesson-2-answers)

;;; Enter your answers in here.

(require '[clojure.string :as string])


(defn find-anagrams
  "step1: create a key and value pairs to replace the original string
   step2: sort the key so that if two strings are anagrams of each other then they have the same value of the key
   step3: merge the vectors which have the same key(means are anagrams of each other) and return the result as a map
   step4,5,6: get rid of keys and fliter out elements which only have one element in it and return the result as a set"
  [vec]
  (->> (map (fn [str] [str str]) vec)
       (map (fn [[key value]] {(sort key) value}))
       (reduce #(merge-with (fn [v1 v2] (if (set? v1)
                                          (conj v1 v2)
                                          #{v1 v2})) %1 %2))
       (vals)
       (filter #(set? %))
       (set)))


(= (find-anagrams ["meat" "mat" "team" "mate" "eat"])
   #{#{"meat" "team" "mate"}})

(= (find-anagrams ["veer" "lake" "item" "kale" "mite" "ever"])
   #{#{"veer" "ever"} #{"lake" "kale"} #{"mite" "item"}})

