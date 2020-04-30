(ns clojure-training.lesson05
  (:require
   [clojure.pprint :as pprint]
   [clojure.java.io :as io]))

;;;; Java Interop


;;;; I/O

;; slurp, spit

;; The reader
read-string

;; The writer
pr prn
clojure.pprint/pprint
clojure.pprint/cl-format ; hardcore!  See the Hyperspec.

with-open

io/reader
io/writer

io/resource


with-out-str  with-in-str
