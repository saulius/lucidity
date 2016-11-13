(ns lucid.aether.artifact-test
  (:use hara.test)
  (:require [lucid.aether.artifact]
            [hara.io.classpath :as classpath]
            [hara.object :as object])
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

^{:refer lucid.aether.artifact/default-artifact :added "1.2"}
(comment "creates `DefaultArtifact` from a string"

  (object/from-data "im.chit:hara:2.4.8" DefaultArtifact)
  ;;=> #artifact "im.chit:hara:jar:2.4.8"
  )
