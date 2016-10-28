(ns lucid.distribute.manifest.graph.external-test
  (:use hara.test)
  (:require [clojure.set :as set]
            [lucid.distribute.manifest.graph
             [internal :as internal]
             [external :refer :all]]
            [hara.io.project :as project]
            [lucid.distribute.manifest [common :refer [build-filemap]] source]))

(def ^:dynamic *project*
  (project/project "example/distribute.advance/project.clj"))

(def ^:dynamic *filemap*
  (->> (:distribute *project*)
       (map #(build-filemap (:root *project*) %))
       (apply merge-with set/union)))

(def ^:dynamic *i-deps*
  (merge-with set/union
              (internal/resource-dependencies (:distribute *project*))
              (internal/find-all-module-dependencies *filemap*)))

^{:refer lucid.distribute.manifest.graph.external/is-clojure? :added "1.2"}
(fact "checks if the coordinate is clojure"

  (is-clojure? '[org.clojure/clojure "1.6.0"])
  => true)

^{:refer lucid.distribute.manifest.graph.external/to-jar-entry :added "1.2"}
(fact "constructs a jar entry"

  (to-jar-entry '[:clj vinyasa.maven.file])
  => "vinyasa/maven/file.clj"

  (to-jar-entry '[:cljs vinyasa.maven.file])
  => "vinyasa/maven/file.cljs"

  (to-jar-entry '[:clj version-clj.core])
  => "version_clj/core.clj")

^{:refer lucid.distribute.manifest.graph.external/resolve-with-ns :added "1.2"}
(fact "finds the maven coordinate for a given namespace"
  
  (resolve-with-ns '[:clj vinyasa.maven.file]
                   (:dependencies *project*)
                   *project*)
  => '[im.chit/vinyasa.maven "0.3.1"])

^{:refer lucid.distribute.manifest.graph.external/find-external-imports :added "1.2"}
(fact "finds external imports for a given submodule"
  
  (find-external-imports *filemap* *i-deps* "core")
  => '#{[:clj vinyasa.maven.file]})

^{:refer lucid.distribute.manifest.graph.external/find-all-external-imports :added "1.2"}
(fact  "finds external imports for the filemap"
  
  (find-all-external-imports *filemap* *i-deps* *project*)
  => {"web" #{},
      "util.data" #{},
      "util.array" #{},
      "jvm" #{}
      "core" '#{[im.chit/vinyasa.maven "0.3.1"]},
      "common" #{},
      "resources" #{}})
