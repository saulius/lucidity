(ns lucid.distribute.manifest
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [hara.io.project :as project]
            [lucid.distribute.manifest.graph
             [internal :as internal]
             [external :as external]]
            [lucid.distribute.analyser :as analyser]
            [lucid.distribute.manifest [common :refer [build-filemap]] source]
            [lucid.distribute.common :refer [map->FileInfo]]))

(def ^:dynamic *default-config*
  [{:type :clojure
    :path "src"
    :levels 1}])

(defn clj-version
  "returns the clojure version of a project
 
   (clj-version (project/project \"example/distribute.advance/project.clj\"))
   => \"1.6.0\""
  {:added "1.2"}
  [project]
  (->> (:dependencies project)
       (filter #(= (first %) 'org.clojure/clojure))
       (first)
       (second)))

(defn create-root-entry
  "creates the root entry"
  {:added "1.2"}
  [project branches]
  (-> (select-keys project [:name :group :version :dependencies])
      (update-in [:dependencies] #(apply conj (vec %) (map :coordinate branches)))
      (assoc :files [])))

(defn create-branch-entry
  "creates the individual branch entry"
  {:added "1.2"}
  [project filemap i-deps ex-deps pkg]
  (let [{:keys [version group] base :name} project
        name (str group "/" base "." pkg)]
    {:coordinate [(symbol name) version]
     :files (mapv :path (get filemap pkg))
     :dependencies (->> (get i-deps pkg)
                        (map (fn [k]
                               [(symbol (str group "/" base "." k)) version]))
                        (concat [['org.clojure/clojure (clj-version project)]]
                                (filter identity (get ex-deps pkg)))
                        vec)
     :version version
     :name name
     :group group}))

(defn create
  "creates a manifest for further processing
   
   (-> (project/project \"example/distribute.advance/project.clj\")
       (create)
       :root)
   => '{:name blah,
        :version \"0.1.0-SNAPSHOT\",
        :dependencies [[org.clojure/clojure \"1.6.0\"]
                       [blah/blah.common \"0.1.0-SNAPSHOT\"]
                       [blah/blah.core \"0.1.0-SNAPSHOT\"]
                       [blah/blah.util.array \"0.1.0-SNAPSHOT\"]
                       [blah/blah.util.data \"0.1.0-SNAPSHOT\"]
                       [blah/blah.web \"0.1.0-SNAPSHOT\"]
                       [blah/blah.jvm \"0.1.0-SNAPSHOT\"]
                       [blah/blah.resources \"0.1.0-SNAPSHOT\"]],
        :files []}"
  {:added "1.2"}
  [project]
  (let [cfgs (or (:distribute project) *default-config*)
        cfgs (if (vector? cfgs) cfgs [cfgs])
        filemap   (->> cfgs
                       (map #(build-filemap (:root project)
                                            (merge (select-keys project [:jar-exclusions]) %)))
                       (apply merge-with set/union))
        i-deps (merge-with set/union
                           (internal/resource-dependencies cfgs)
                           (internal/find-all-module-dependencies filemap))
        ex-deps  (external/find-all-external-imports filemap i-deps project)
        ks       (keys filemap)
        branches (mapv #(create-branch-entry project filemap i-deps ex-deps %) ks)]
    {:root (create-root-entry project branches)
     :branches (zipmap ks branches)}))
