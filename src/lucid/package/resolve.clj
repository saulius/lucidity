(ns lucid.package.resolve
  (:require [lucid.aether :as aether]
            [hara.io.classpath :as classpath]))

(defn dependencies
  "list dependencies for an artifact
 
   (dependencies [['org.clojure/core.match *match-version*]])
   => []"
  {:added "1.1"}
  [coordinates opts]
  (->> coordinates
       (map (fn [coord]
              (aether/resolve-dependencies coord opts)))
       (apply set/union)
       vec))