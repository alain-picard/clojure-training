(ns src.assignments.phat.aoc2019-day3
  (:require [clojure.string :as s]
            [clojure.set :as clj-set]))

;; https://adventofcode.com/2019/day/3
;; there are two wires starting from the same central port.
;; they are tangled together with many intersections
;; find the closest intersection to the port using Manhattan distance

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; part 1
(def filepath "src/assignments/phat/tmp/adventofcode-day3-input.txt")
(def port [0 0])


(defn parse-int
  "Returns an integer from a string"
  [s]
  (read-string (re-find #"\d+" s)))


(defn locate-position
  "Takes a coordinate and a move instruction
  and returns coordinate of the new location after the move"
  [[x y] move]
  (assert (some #{(first move)} '(\L \R \U \D))
          "Only take valid instruction starting with L, R, U or D")
  (cond (= \L (first move)) [(- x (parse-int move)) y]
        (= \R (first move)) [(+ x (parse-int move)) y]
        (= \U (first move)) [x (+ y (parse-int move))]
        (= \D (first move)) [x (- y (parse-int move))]))


(defn generate-coordinates
  "Takes a sequence of move instructions
  and returns a sequence of coordinates of vectices on a moving path"
  [coll]
  (loop [location port
         path coll
         cords []]
    (if (empty? path)
      cords
      (recur (locate-position location (first path))
             (rest path)
             (conj cords location)))))


(defn manhattan-distance
  "Returns mahattan distance between two coordinates"
  [[x-origin y-origin] [x-target y-target]]
  (+ (Math/abs (- x-target x-origin)) 
     (Math/abs (- y-target y-origin))))


(defn line-segment
  "Returns all points belonging to a line between two vertices"
  [[x1 y1] [x2 y2]]
  (let [x-step (if (> x1 x2) -1 1)
        y-step (if (> y1 y2) -1 1)]
    (for [x (range x1 (+ x-step x2) x-step)
          y (range y1 (+ y-step y2) y-step)]
      [x y])))


(defn points-of-path
  "Takes a collection of vertices of a path
  and returns a new collection of every points lying on that path"
  [vertices]
  (loop [vertex vertices
         points []]
    (if (= 1 (count vertex))
      points
      (recur (rest vertex)
             (concat points (line-segment (first vertex) (second vertex)))))))


(defn shortest-distance
  "Returns the shortest manhattan from an intersection to the central port"
  [s]
  (let [wires (->> (s/split s #"\n")
                   (map #(s/replace % #"\r" ""))
                   (map #(s/split % #","))
                   (map generate-coordinates)
                   (map points-of-path))]
    (->> (clj-set/intersection 
          (disj (set (first wires)) [0 0]) 
          (disj (set (second wires)) [0 0]))
         (map #(manhattan-distance port %))
         (apply min))))


;; 5783 215 831 723 238
;; cant get a correct result from input
(def input (slurp filepath))
;; the program will do some heavy lifting here!!!
(shortest-distance input)
; => 2000 is wrong

;; but get correct test results???
(def s1 "R75,D30,R83,U83,L12,D49,R71,U7,L72\r\nU62,R66,U55,R34,D71,R55,D58,R83")
(shortest-distance s1)
;=> 159

(def s2 "R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51\nU98,R91,D20,R16,D67,R40,U7,R15,U6,R7")
(shortest-distance s2)
;=> 135
