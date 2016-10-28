(ns lucid.distribute.graph.internal-test
  (:use midje.sweet)
  (:require [lucid.distribute.graph.internal :refer :all]
            [leiningen.core.project :as project]
            [lucid.distribute.manifest
             [common :refer [build-filemap]] source]
            [lucid.distribute.analyser java clj cljs]
						[clojure.set :as set]))

(def ^:dynamic *config*
  (-> (project/read "example/repack.advance/project.clj")
      (project/unmerge-profiles [:default])
      :repack))

(def ^:dynamic *files*
  (->> *config*
       (map #(build-filemap "example/repack.advance" %))
       (apply merge-with set/union)))

^{:refer lucid.distribute.graph.internal/find-all-module-dependencies :added "1.2"}
(fact "finds all internal module dependencies through analysis of :imports and :exports"

 (find-all-module-dependencies *files*)
  => {"resources" #{},
      "jvm" #{},
      "common" #{},
      "core" #{},
      "util.array" #{},
      "util.data" #{"util.array"},
      "web" #{"core" "common" "util.array"}})

^{:refer lucid.distribute.graph.internal/resource-dependencies :added "1.2"}
(fact "looks at the config to see if there are any explicitly stated
  dependencies. Used for packaging the resources folder that may be
  referenced by different modules after repack"

  (resource-dependencies *config*)
  => {"core" #{"resources"}})
