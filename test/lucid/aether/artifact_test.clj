(ns lucid.aether.artifact-test
  (:use hara.test)
  (:require [lucid.aether.artifact]
            [hara.io.classpath :as classpath])
  (:import [org.eclipse.aether.artifact Artifact  DefaultArtifact]))

^{:refer hara.io.classpath/artifact :added "1.2"}
(fact "added `:eclipse` keyword for `Artifact` creation"

  (classpath/artifact :eclipse "im.chit:hara.test:2.4.8")
  => DefaultArtifact
       
  (->> "im.chit:hara.test:2.4.8"
       (classpath/artifact :eclipse)
       (classpath/artifact)
       (into {}))
  => {:group "im.chit",
      :artifact "hara.test",
      :extension "jar",
      :classifier "",
      :version "2.4.8"}

  (->> '[im.chit/hara.test "2.4.8"]
       (classpath/artifact :eclipse)
       (classpath/artifact)
       (into {}))
  => {:group "im.chit",
      :artifact "hara.test",
      :extension "jar",
      :classifier "",
      :version "2.4.8"})
