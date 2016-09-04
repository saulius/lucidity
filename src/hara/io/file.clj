(ns hara.io.file
  (:require [clojure.java.io :as io]
            [hara.namespace.import :as ns])
  (:import java.io.File))
  
(ns/import hara.io.file.basic :all)

(def ^:dynamic *cwd* (.getCanonicalPath (io/file ".")))

(def ^:dynamic *sep* (System/getProperty "file.separator"))

(def ^:dynamic *home* (System/getProperty "user.home"))

(def ^:dynamic *tmp-dir (System/getProperty "java.io.tmpdir"))

(defn ^File file
  [f]
  (cond (instance? File f)
        (.getCanonicalFile ^File f)
        
        (string? f)
        (let [path (if (.startsWith ^String f (str "~" *sep*))
                     (.replace ^String f "~" ^String *home*)
                     f)]
          (.getCanonicalFile (io/file path)))))

(defn path
  [f]
  (.getAbsolutePath (file f)))

(defn list-files
  [path]
  (seq (.listFiles (file path))))

(defn list-all-files
  [path]
  (tree-seq directory? list-files (file path)))

(defn pushback
  [io]
  (let [reader (cond (instance? java.io.Reader io) io
                     
                     (instance? java.io.InputStream io)
                     (io/reader io)

                     :else
                     (io/reader (file io)))]
    (java.io.PushbackReader. reader)))

(defn source-seq
  [path]
  (let [reader (pushback path)]
    (take-while identity
                (repeatedly #(try (read reader)
                                  (catch Throwable e))))))

(comment
  (count (filter directory? (list-dir-tree "."))))
