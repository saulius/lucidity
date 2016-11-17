(ns lucid.distribute.manifest.graph.internal-test
  (:use hara.test)
  (:require [lucid.distribute.manifest.graph.internal :refer :all]
            [hara.io.project :as project]
            [lucid.distribute.manifest
             [common :refer [build-filemap]] source]
            [lucid.distribute.analyser java clj cljs]
            [clojure.set :as set]))

(def ^:dynamic *config*
  (-> (project/project "example/distribute.advance/project.clj")
      :distribute
      :files))

(def ^:dynamic *files*
  (->> *config*
       (map #(build-filemap "example/distribute.advance" %))
       (apply merge-with set/union)))

^{:refer lucid.distribute.manifest.graph.internal/find-module-dependencies :added "1.2"}
(fact "finds internal module dependencies"

  (def tally
    (reduce-kv (fn [i k v]
                 (assoc i k {:imports (apply set/union (map :imports v))
                             :exports (apply set/union (map :exports v))}))
               {}
               *files*))

  (find-module-dependencies
   "web"
   '{:imports #{[:clj repack.util.array]
                [:cljs repack.web.client]
                [:class im.chit.repack.common.Hello]
                [:clj repack.core]}
     :exports #{[:clj repack.web]
                [:class repack.web.client.Main]
                [:class repack.web.client.Client]
                [:clj repack.web.client]
                [:cljs repack.web]
                [:class im.chit.repack.web.Client]}}
   tally)
  => #{"core" "util.array" "common"})

^{:refer lucid.distribute.manifest.graph.internal/find-all-module-dependencies :added "1.2"}
(fact "finds all internal module dependencies through analysis of :imports and :exports"

 (find-all-module-dependencies *files*)
  => {"resources" #{},
      "jvm" #{},
      "common" #{},
      "core" #{},
      "util.array" #{},
      "util.data" #{"util.array"},
      "web" #{"core" "common" "util.array"}})

^{:refer lucid.distribute.manifest.graph.internal/resource-dependencies :added "1.2"}
(fact "looks at the config to see if there are any explicitly stated dependencies."

  (resource-dependencies *config*)
  => {"core" #{"resources"}})
