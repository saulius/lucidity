(ns lucid.core.aether.artifact-test
  (:use hara.test)
  (:require [lucid.core.aether.artifact :refer :all])
  (:import [org.eclipse.aether.artifact Artifact  DefaultArtifact]))

^{:refer lucid.core.aether.artifact/artifact->vector :added "1.1"}
(fact "converts an artifact to the clojure vector notation"

  (-> (DefaultArtifact. "im.chit:hara.test:2.4.4")
      (artifact->vector))
  => '[im.chit/hara.test "2.4.4"])

^{:refer lucid.core.aether.artifact/artifact<-vector :added "1.1"}
(fact "creates an artifact from the clojure vector notation"

  (-> (artifact<-vector '[im.chit/hara.test "2.4.4"])
      (str))
  => "im.chit:hara.test:jar:2.4.4")

^{:refer lucid.core.aether.artifact/artifact->string :added "1.1"}
(fact "converts an artifact to the string notation"
  
  (-> (DefaultArtifact. "im.chit:hara.test:2.4.4")
      (artifact->string))
  => "im.chit:hara.test:jar:2.4.4")

^{:refer lucid.core.aether.artifact/artifact<-string :added "1.1"}
(fact "creates an artifact from the string notation"
  
  (-> (artifact<-string "im.chit:hara.test:2.4.4")
      (str))
  => "im.chit:hara.test:jar:2.4.4")

^{:refer lucid.core.aether.artifact/artifact :added "1.1"}
(fact "idempotent function, converting either a vector or string to an artifact")
