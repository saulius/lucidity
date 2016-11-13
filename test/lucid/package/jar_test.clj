(ns lucid.package.jar-test
  (:use hara.test)
  (:require [lucid.package.jar :refer :all]
            [hara.io.project :as project]))

^{:refer lucid.package.jar/generate-manifest :added "1.2"}
(comment "creates a manifest.mf file for the project"

  (generate-manifest (project/project)))

^{:refer lucid.package.jar/generate-jar :added "1.2"}
(comment "creates a jar file for the project"

  (generate-jar (project/project)))
