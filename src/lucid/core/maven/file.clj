(ns lucid.core.maven.file
  (:require [clojure.string :as string]
            [clojure.java.io :as io])
  (:import [clojure.lang Symbol]))

(defonce ^:dynamic *sep* (System/getProperty "file.separator"))

(defonce ^:dynamic *clojure-loader*
  (or (.getClassLoader clojure.lang.RT)
      (.getContextClassLoader (Thread/currentThread))))

(defonce ^:dynamic *java-class-path*
  (->> (string/split (System/getProperty "java.class.path") #":")
       (filter (fn [x] (.endsWith x ".jar")))))

(defonce ^:dynamic *java-home* (System/getProperty "java.home"))

(defonce ^:dynamic *java-runtime-jar* (str *java-home* "/lib/rt.jar"))

(defn resource-symbol-path
  "creates a path based on symbol
   (resource-symbol-path 'hara.test)
   => \"hara/test.clj\"
 
   (resource-symbol-path 'version-clj.core)
   => \"version_clj/core.clj\""
  {:added "1.1"}
  [sym]
  (let [sym-str (-> (str sym)
                    (.replaceAll "\\." *sep*)
                    (.replaceAll "-" "_"))
        f-char (-> sym-str (string/split (re-pattern *sep*)) last first)]

    (str sym-str
         (if (<= (int \A) (int f-char) (int \Z))
           ".class"
           ".clj"))))

(defn resource-path
  "creates a path based item
   (resource-path \"hello/world.txt\")
   => \"hello/world.txt\"
 
   (resource-path 'version-clj.core)
   => \"version_clj/core.clj\"
 
   (resource-path java.io.File)
   => \"java/io/File.class\""
  {:added "1.1"}
  [x]
  (condp = (type x)
    String x
    Symbol (resource-symbol-path x)
    Class (-> (.getName x)
              (.replaceAll "\\." *sep*)
              (str  ".class"))))
