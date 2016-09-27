(ns lucid.test.common)

(defmulti analyse-file (fn [type file & [opts]] type))