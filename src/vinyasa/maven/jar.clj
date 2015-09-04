(ns vinyasa.maven.jar
  (:require [vinyasa.maven.file :as file]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [version-clj.core :as version])
  (:import [clojure.lang Symbol]))

(defonce ^:dynamic *local-repo*
  (string/join file/*sep* [(System/getProperty "user.home") ".m2" "repository"]))

(defn jar-entry [jar-path entry]
  (let [resource-name (file/resource-path entry)
        file   (io/as-file jar-path)]
    (if (.exists file)
      (.getEntry (java.util.jar.JarFile. file) resource-name))))

(defn jar-contents [jar-path]
  (with-open [zip (java.util.zip.ZipInputStream.
                   (io/input-stream jar-path))]
    (loop [entries []]
      (if-let [e (.getNextEntry zip)]
        (recur (conj entries (.getName e)))
        entries))))

(defmulti resolve-jar (fn [x & [k v]] k))

(defmethod resolve-jar nil
  [x & _]
  (resolve-jar x :classloader file/*clojure-loader*))

(defmethod resolve-jar :classloader
  [x _ loader]
  (if-let [res (-> (file/resource-path x)
                   (io/resource loader))]
    (-> (re-find #"file:(.*)" (.getPath res))
        second
        (clojure.string/split #"!/"))))

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


 (defn maven-file [[name version] & [suffix local-repo]]
   (let [[group artifact] (string/split (str name) #"/")
         artifact (or artifact
                      group)]
     (string/join file/*sep*
                  [(or local-repo *local-repo*) (.replaceAll group "\\." file/*sep*)
                   artifact version (str artifact "-" version (or suffix ".jar"))] )))

(defmethod resolve-jar :coordinate
  [x _ coordinate]
  (resolve-jar x :jar-path (maven-file coordinate)))

(defmethod resolve-jar :coordinates
  [x _ [coordinate & more]]
  (if-not (nil? coordinate)
    (if-let [res (resolve-jar x :coordinate coordinate)]
      res
      (recur x :coordinates more))))

(defn find-all-jars [repo]
  (->> (file-seq (io/as-file repo))
       (filter (fn [f] (-> f (.getName) (.endsWith ".jar"))))
       (reduce (fn [out jar]
                 (let [parent-dir (.getParentFile jar)]
                   (assoc-in out [(.getParent parent-dir) (.getName parent-dir)]
                             (.getPath jar)))) {})))

(defn find-latest-jars [repo]
  (->> (find-all-jars repo)
       (map (fn [[_ entries]]
              (let [versions (keys entries)]
                (get entries (last (sort version/version-compare versions))))))))

(defmethod resolve-jar :repository
  [x _ & [repo]]
  (resolve-jar x :jar-paths (find-latest-jars (or repo *local-repo*))))
