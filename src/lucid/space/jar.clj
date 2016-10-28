(ns lucid.space.jar
  (:require [lucid.space.file :as file]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [version-clj.core :as version])
  (:import [clojure.lang Symbol]))

(defonce ^:dynamic *local-repo*
  (string/join file/*sep* [(System/getProperty "user.home") ".m2" "repository"]))

(defn jar-file
  "returns a path as a jar or nil if it does not exist
 
   (jar-file *match-path*)
   => java.util.jar.JarFile"
  {:added "1.1"}
  [path]
  (let [file   (io/as-file path)]
    (if (.exists file)
      (java.util.jar.JarFile. file))))

(defn jar-entry
  "returns an entry of the jar or nil if it does not exist
 
   (jar-entry *match-path* \"clojure/core/match.clj\")
   => java.util.jar.JarFile$JarFileEntry
 
   (jar-entry *match-path* \"NON-FILE\")
   => nil"
  {:added "1.1"}
  [path entry]
  (if-let [jar (jar-file path)]
    (let [resource-name (file/resource-path entry)
          entry (.getEntry jar resource-name)]
      entry)))

(defn jar-stream
  "gets the input-stream of the entry for the jar
   
   (-> (java.io.File. *match-path*)
       (jar-stream \"clojure/core/match.clj\")
       (java.io.InputStreamReader.)
       (java.io.PushbackReader.)
       (read)
       second)
   => 'clojure.core.match"
  {:added "1.1"}
  [path entry]
  (if-let [jar (jar-file path)]
    (let [resource-name (file/resource-path entry)
          entry (.getEntry jar resource-name)]
      (.getInputStream jar entry))))

(defn jar-contents
  "lists the contents of a jar
   
   (-> (java.io.File. *match-path*)
       (jar-contents))
   => (contains [\"clojure/core/match.clj\"] :in-any-order :gaps-ok)"
  {:added "1.1"}
  [path]
  (with-open [zip (java.util.zip.ZipInputStream.
                   (io/input-stream path))]
    (loop [entries []]
      (if-let [e (.getNextEntry zip)]
        (recur (conj entries (.getName e)))
        entries))))

(defmulti resolve-jar
  "resolves the path of a jar for a given namespace, according to many options
   
   (resolve-jar 'clojure.core.match)
   => [*match-path* \"clojure/core/match.clj\"]
 
   "
  {:added "1.1"}
  (fn [x & [k v]] k))

(defmethod resolve-jar nil
  [x & _]
  (resolve-jar x :classloader file/*clojure-loader*))

(defmethod resolve-jar :classloader
  [x _ loader]
  (if-let [res (-> (file/resource-path x)
                   (io/resource loader))]
    (-> #"file:(.*)"
        (re-find (.getPath res))
        second
        (clojure.string/split #"\!/"))))

(defmethod resolve-jar :jar-path
  [x _ jar-file]
  (let [res-path (file/resource-path x)]
    (if (jar-entry jar-file res-path)
      [(str jar-file) res-path])))

(defmethod resolve-jar :jar-paths
  [x _ [jar-path & more]]
  (if-not (nil? jar-path)
    (if-let [res (resolve-jar x :jar-path jar-path)]
      res
      (recur x :jar-path more))))

(defn maven-file
  "returns the path of the local maven file
   (maven-file ['org.clojure/core.match *match-version*])
   => *match-path*"
  {:added "1.1"}
  [[name version] & [suffix local-repo]]
  (let [[group artifact] (string/split (str name) #"/")
        artifact (or artifact
                     group)]
    (string/join file/*sep*
                 [(or local-repo *local-repo*) (.replaceAll group "\\." file/*sep*)
                  artifact version (str artifact "-" version (or suffix ".jar"))])))

(defmethod resolve-jar :coordinate
  [x _ coordinate]
  (resolve-jar x :jar-path (maven-file coordinate)))

(defmethod resolve-jar :coordinates
  [x _ [coordinate & more]]
  (if-not (nil? coordinate)
    (if-let [res (resolve-jar x :coordinate coordinate)]
      res
      (recur x :coordinates more))))

(defn find-all-jars
  "returns all jars within a repo in a form of a map
   (-> (find-all-jars (str (fs/path \"~/.m2/repository\")))
       (get (str (fs/path \"~/.m2/repository/org/clojure/core.match\")))
       (get *match-version*))
   => *match-path*"
  {:added "1.1"}
  [repo]
  (->> (file-seq (io/as-file repo))
       (filter (fn [f] (-> f (.getName) (.endsWith ".jar"))))
       (reduce (fn [out jar]
                 (let [parent-dir (.getParentFile jar)]
                   (assoc-in out [(.getParent parent-dir) (.getName parent-dir)]
                             (.getPath jar)))) {})))

(defn find-latest-jars
  "returns the latest jars within a repo
   (->> (find-latest-jars (str (fs/path \"~/.m2/repository\")))
        (filter #(= % *match-path*))
        first)
   => *match-path*"
  {:added "1.1"}
  [repo]
  (->> (find-all-jars repo)
       (map (fn [[_ entries]]
              (let [versions (keys entries)]
                (get entries (last (sort version/version-compare versions))))))))

(defmethod resolve-jar :repository
  [x _ & [repo]]
  (resolve-jar x :jar-paths (find-latest-jars (or repo *local-repo*))))
