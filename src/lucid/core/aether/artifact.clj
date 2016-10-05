(ns lucid.core.aether.artifact
  (:import [org.eclipse.aether.artifact Artifact  DefaultArtifact]))

(defn artifact->vector
  "converts an artifact to the clojure vector notation
 
   (-> (DefaultArtifact. \"im.chit:hara.test:2.4.4\")
       (artifact->vector))
   => '[im.chit/hara.test \"2.4.4\"]"
  {:added "1.1"}
  [artifact]
  [(symbol (str (.getGroupId artifact)
                "/"
                (.getArtifactId artifact)))
   (.getVersion artifact)])

(defn artifact<-vector
  "creates an artifact from the clojure vector notation
 
   (-> (artifact<-vector '[im.chit/hara.test \"2.4.4\"])
       (str))
   => \"im.chit:hara.test:jar:2.4.4\""
  {:added "1.1"}
  [[blob version]]
  (let [name (.getName blob)
        nsp  (or (.getNamespace blob)
                 name)]
    (DefaultArtifact. (str nsp ":" name ":" version))))

(defn artifact->string
  "converts an artifact to the string notation
   
   (-> (DefaultArtifact. \"im.chit:hara.test:2.4.4\")
       (artifact->string))
   => \"im.chit:hara.test:jar:2.4.4\""
  {:added "1.1"}
  [artifact]
  (str artifact))

(defn artifact<-string
  "creates an artifact from the string notation
   
   (-> (artifact<-string \"im.chit:hara.test:2.4.4\")
       (str))
   => \"im.chit:hara.test:jar:2.4.4\""
  {:added "1.1"}
  [s]
  (DefaultArtifact. s))

(defn artifact
  "idempotent function, converting either a vector or string to an artifact"
  {:added "1.1"}
  [rep]
  (cond
    (vector? rep) (artifact<-vector rep)
    (string? rep) (artifact<-string rep)
    (instance? Artifact rep) artifact
    :else (throw (Exception. (str "Cannot convert " rep " to artifact.")))))
