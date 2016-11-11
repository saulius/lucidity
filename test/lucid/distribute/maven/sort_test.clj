(ns lucid.distribute.maven.sort-test
  (:use hara.test)
  (:require [lucid.distribute.maven.sort :refer :all]
            [lucid.distribute.manifest :as manifest]
            [hara.io.project :as project]))

(def MANIFEST
  (-> (project/project "example/distribute.advance/project.clj")
      (manifest/create)))

^{:refer lucid.distribute.maven.sort/all-branch-nodes :added "1.2"}
(comment "returns all nodes in the branche"

  (all-branch-nodes MANIFEST)
  => ({:coordinate [blah/blah.common "0.1.0-SNAPSHOT"],
       :dependencies [[org.clojure/clojure "1.6.0"]],
       :id "common"}
      
      ...

      {:coordinate [blah/blah.resources "0.1.0-SNAPSHOT"],
       :dependencies [[org.clojure/clojure "1.6.0"]],
       :id "resources"}))

^{:refer lucid.distribute.maven.sort/all-branch-deps :added "1.2"}
(comment "returns all internal dependencies"

  (all-branch-deps MANIFEST)
  => #{[blah/blah.util.data "0.1.0-SNAPSHOT"]
       [blah/blah.util.array "0.1.0-SNAPSHOT"]
       [blah/blah.resources "0.1.0-SNAPSHOT"]
       [blah/blah.web "0.1.0-SNAPSHOT"]
       [blah/blah.core "0.1.0-SNAPSHOT"]
       [blah/blah.common "0.1.0-SNAPSHOT"]
       [blah/blah.jvm "0.1.0-SNAPSHOT"]})

^{:refer lucid.distribute.maven.sort/topsort-branch-deps-pass :added "1.2"}
(comment "single topsort pass")

^{:refer lucid.distribute.maven.sort/topsort-branch-deps :added "1.2"}
(comment "sorts and arranges dependencies in order of deployment"

  (topsort-branch-deps MANIFEST)
  => [[{:coordinate [blah/blah.common "0.1.0-SNAPSHOT"],
        :dependencies [[org.clojure/clojure "1.6.0"]],
        :id "common"}
       
       ...
       
       {:coordinate [blah/blah.web "0.1.0-SNAPSHOT"],
        :dependencies [[org.clojure/clojure "1.6.0"]
                       [blah/blah.core "0.1.0-SNAPSHOT"]
                       [blah/blah.util.array "0.1.0-SNAPSHOT"]
                       [blah/blah.common "0.1.0-SNAPSHOT"]],
        :id "web"}]])
