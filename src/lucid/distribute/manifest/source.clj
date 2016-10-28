(ns lucid.distribute.manifest.source
  (:require [clojure.java.io :as io]
            [hara.io.file :as fs]
            [lucid.distribute.manifest.common :as manifest]
            [clojure.string :as string]))

(defn child-dirs
  "lists all the child directories for a particular folder
   (-> (io/file \"example\")
       (child-dirs)
       sort)
   => [\"distribute.advance\"
       \"distribute.simple\"]"
  {:added "1.2"}
  [path]
  (let [children (seq (.list path))]
    (->> children
         (filter (fn [chd] (.isDirectory (io/file path chd)))))))

(defn split-path
  "splits the file into its path components
   
   (split-path \"repack/example/hello.clj\")
   =>  [\"repack\" \"example\" \"hello\"]"
  {:added "1.2"}
  [path]
  (let [idx (.lastIndexOf path ".")]
    (->> (.substring path 0 idx)
         (fs/section)
         (.iterator)
         (iterator-seq)
         (mapv str))))

(defn group-by-package
  "groups the source files by package
   (->> [\"common.clj\"
         \"core.clj\"
         \"util/array/sort.clj\"
         \"util/array.clj\"
         \"util/data.clj\"
         \"web/client.clj\"
         \"web.clj\"]
       (mapv #(java.io.File.
                (str \"example/distribute.advance/src/clj/repack/\" %)))
        (group-by-package {:root (.toFile (fs/path \"example/distribute.advance/src/clj/repack\"))
                           :type :clojure
                           :levels 2
                           :path \"src/clj\"
                           :standalone #{\"web\"}}))
   => {\"common\" #{\"common.clj\"},
       \"core\" #{\"core.clj\"},
       \"util.array\" #{\"util/array/sort.clj\" \"util/array.clj\"},
       \"util.data\" #{\"util/data.clj\"},
       \"web\" #{\"web.clj\" \"web/client.clj\"}}"
  {:added "1.2"}
  [opts files]
  (let [lvl (or (:levels opts) 1)]

    (reduce (fn [i f]
              (let [rpath (str (fs/relativize (:root opts) f))
                    v   (split-path rpath)
                    pkg (take lvl v)
                    pkg (if (get (:standalone opts) (first pkg))
                          (first pkg)
                          (string/join "." pkg))]
                (update-in i [pkg] (fnil #(conj % rpath) #{rpath} ))))
            {}  files)))

(defmethod manifest/build-filemap :clojure
  [project-dir cfg]
  (let [src-path (:path cfg)
        src-dir (io/file project-dir src-path)
        root-path (or (:root cfg)
                      (let [ds (child-dirs src-dir)]
                        (if (= (count ds) 1)
                          (first ds)
                          (throw (Exception. (str "More than one possible root: " ds))))))
        root-dir (io/file src-dir root-path)
        distro   (->> root-dir
                      (file-seq)
                      (filter (fn [f] (not (.isDirectory f))))
                      (group-by-package (assoc cfg :root root-dir)))]
    (manifest/create-filemap distro {:root project-dir
                                     :folder (str src-path "/" root-path)
                                     :pnil "default"})))
