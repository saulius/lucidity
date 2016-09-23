(ns lucid.unit.common)

(defmulti analyse-file (fn [type file & [opts]] type))
