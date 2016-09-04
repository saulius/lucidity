(ns lucid.dive.analyse.common)

(defmulti analyse-file (fn [type file & [opts]] type))
