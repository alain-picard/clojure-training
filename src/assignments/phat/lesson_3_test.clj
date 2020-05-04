(ns lesson3-answer.test
  (:require [src.assignments.phat.lesson-3-answers :refer [recognize-card best-hand]]
            [clojure.test :refer (deftest is are run-tests)]))

(deftest test-recognize-card
  (is (= {:suit :diamond :rank 10} (recognize-card "DQ")))
  (is (= {:suit :heart :rank 3} (recognize-card "H5")))
  (is (= {:suit :club :rank 12} (recognize-card "CA")))
  (is (= (range 13) (map (comp :rank recognize-card str)
                         '[S2 S3 S4 S5 S6 S7
                           S8 S9 ST SJ SQ SK SA]))))

(deftest test-best-hand
  (is (= :high-card (best-hand ["HA" "D2" "H3" "C9" "DJ"])))
  (is (= :pair (best-hand ["HA" "HQ" "SJ" "DA" "HT"])))
  (is (= :two-pair (best-hand ["HA" "DA" "HQ" "SQ" "HT"])))
  (is (= :three-of-a-kind (best-hand ["HA" "DA" "CA" "HJ" "HT"])))
  (is (= :straight (best-hand ["HA" "DK" "HQ" "HJ" "HT"])))
  (is (= :straight (best-hand ["HA" "H2" "S3" "D4" "C5"])))
  (is (= :flush (best-hand ["HA" "HK" "H2" "H4" "HT"])))
  (is (= :full-house (best-hand ["HA" "DA" "CA" "HJ" "DJ"])))
  (is (= :four-of-a-kind (best-hand ["HA" "DA" "CA" "SA" "DJ"])))
  (is (= :straight-flush (best-hand ["HA" "HK" "HQ" "HJ" "HT"]))))

(run-tests)

