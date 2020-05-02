(ns clojure-training.lesson03-test
  (:require [clojure-training.lesson03 :refer [anagram?] :as sut]
            [clojure.test :refer [deftest is are]]))


(deftest clojure-can-add
  (is (= 2 (+ 1 1))))



(deftest correct-anagram-test
  (are [x y] (anagram? x y)
    "item" "mite"
    "foo" "oof"
    "bozo" "zobo")

  (is  (anagram? "item" "mite") "Correctly finds anagram")
  (is (anagram? "item" "time"))
  (is (not (anagram? "item" "itemitem"))
      "Correctly rejects same set of letters, but not anagrams"))
