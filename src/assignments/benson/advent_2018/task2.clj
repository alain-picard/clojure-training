(ns assignments.benson.advent-2018.task2)

;;;; ------------------ Part 1 ------------------

(def task2input (slurp "src/assignments/benson/advent_2018/task2input"))

(def ids (clojure.string/split-lines task2input))

(defn match-n 
  "returns if id has N matching letters"
  [n id]
  (contains? (set (vals (frequencies id))) n))


(assert (match-n 2 "abcdec"))
(assert (not (match-n 2 "abcdef")))
(assert (match-n 3 "accdec"))
(assert (not (match-n 3 "abcdef")))
(assert (and (match-n 2 "aacbbb") (match-n 3 "aacbbb")))

;; (defn calculate-checksum 
;; "Returns the checksum calculated by multiplying the count of ID's with 2 matching letters
;;  and ID's with 3 matching letters"
;;   [ids]
;;   (loop [[curr-id & rest-ids] ids
;;          two-matches 0
;;          three-matches 0]
;;     (if curr-id
;;       (recur rest-ids
;;              (if (match-2 curr-id) (inc two-matches) two-matches) 
;;              (if (match-3 curr-id) (inc three-matches) three-matches))
;;       (* two-matches three-matches))))

(defn match-count 
  "Returns count of IDS with N matches"
  [ids n]
    (reduce (fn [matches id]
              (if (match-n n id) (inc matches) matches))
            0
            ids))

(defn calculate-checksum
  "Returns the checksum calculated by multiplying the count of ID's with 2 matching letters
   and ID's with 3 matching letters"
  [ids]
  (* (match-count ids 2) (match-count ids 3)))

; (calculate-checksum ids) -> 4980

;;;; ------------------ Part 2 ------------------

(def id-length (count (first ids)))

(defn non-matches
  "Returns how many letters in ID-1 don't match with its corresponding 
   letter in the other ID-2"
  [id-1 id-2]
  (count (filter false? (map = id-1 id-2))))

(assert (= 3 (non-matches "abcdefg" "abcOOOg")))

;; (defn get-similar-ids
;;   "Returns the ID's with only 1 character difference"
;;   [ids]
;;   (let [similar-ids (promise)]
;;     (doseq [id-1 ids
;;             id-2 ids]
;;       (future (if (= 1 (non-matches id-1 id-2))
;;                 (deliver similar-ids [id-1 id-2]))))
;;     @similar-ids))

(def similar-ids (atom nil))

(defn get-similar-ids
  "Returns the ID's with only 1 character difference"
  [ids]
  (future (doseq [id-1 ids
                  id-2 ids]
            (if (= 1 (non-matches id-1 id-2))
              (reset! similar-ids [id-1 id-2]))))
  @similar-ids)

; #<Atom@678f7e25: ["qysdtrkloagnxfozuwujmhrbvx" "qysdtrkloagnpfozuwujmhrbvx"]>

(defn parse-correct-id 
  "Returns the correct id (all matching characters of ID-1 and ID-2)"
  [[id-1 id-2]]
  (apply str (map #(if (= %1 %2) %1) id-1 id-2)))

; (parse-correct-id @similar-ids) => "qysdtrkloagnfozuwujmhrbvx"