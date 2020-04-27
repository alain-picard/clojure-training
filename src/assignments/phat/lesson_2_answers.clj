(ns lesson-2-answers)

;;;;;;;;;;;;;;;;;
;; first approach

;; split each string into characters => list char-array
;; sort list and compare
;; if two lists equal than add the item to anagram vector
(defn is_anagram
  [string1 string2]
  (if (= (sort (apply list (char-array string1))) 
         (sort (apply list (char-array string2))))
    true
    false))

;; >>> another function to utilise is_anagram
;; not done

;;;;;;;;;;;;;;;;;;
;; second solution

;; convert each item into a set
(defn find_anagram 
  [vector] 
  (set 
   (map set (vals (group-by set vector)))))

;; solve the given problem
(= (find_anagram ["meat" "mat" "team" "mate" "eat"])
   #{#{"meat" "team" "mate"}})

(= (find_anagram ["veer" "lake" "item" "kale" "mite" "ever"])
   #{#{"veer" "ever"} #{"lake" "kale"} #{"mite" "item"}})

;; but not this one
(= (find_anagram ["amazon" "ammazon"])
   #{#{"amazon"} #{"ammazon"}})

