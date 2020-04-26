(ns lesson-2-answers)

;;; Enter your answers in here.

(require '[clojure.string :as string])

(defn find-anagrams
  [vec]
  (->> (map (fn [str] [str str]) vec)
       (map (fn [[key value]] {(#(clojure.string/join "" %) (sort (#(string/split % #"") key))) value}))
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