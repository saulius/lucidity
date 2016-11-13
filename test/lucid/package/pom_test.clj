(ns lucid.package.pom-test
  (:use hara.test)
  (:require [lucid.package.pom :refer :all]
            [hara.io.project :as project]))

^{:refer lucid.package.pom/pom-properties :added "1.2"}
(comment "creates a pom.properties file"

  (pom-properties (project/project)))

^{:refer lucid.package.pom/coordinate->dependency :added "1.2"}
(fact "creates a hiccup dependency entry"

  (coordinate->dependency '[im.chit/hara "0.1.1"])
  => [:dependency
      [:groupId "im.chit"]
      [:artifactId "hara"]
      [:version "0.1.1"]])

^{:refer lucid.package.pom/pom-xml :added "1.2"}
(comment "creates a pom.properties file"

  (pom-xml (project/project)))


^{:refer lucid.package.pom/generate-pom :added "1.2"}
(comment "generates all the pom information for the project"

  (pom-xml (project/project)))
