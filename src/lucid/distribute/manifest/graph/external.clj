(ns lucid.distribute.manifest.graph.external
  (:require [lucid.space.file :refer [*sep*]]
            [lucid.space :as maven]
            [clojure.set :as set]
            [clojure.string :as string]))

(defn is-clojure?
  "checks if the coordinate is clojure
 
   (is-clojure? '[org.clojure/clojure \"1.6.0\"])
   => true"
  {:added "1.2"}
  [coordinate]
  (= (first coordinate) 'org.clojure/clojure))

(defn to-jar-entry
  "constructs a jar entry
 
   (to-jar-entry '[:clj vinyasa.maven.file])
   => \"vinyasa/maven/file.clj\"
 
   (to-jar-entry '[:cljs vinyasa.maven.file])
   => \"vinyasa/maven/file.cljs\"
 
   (to-jar-entry '[:clj version-clj.core])
   => \"version_clj/core.clj\""
  {:added "1.2"}
  [[type sym]]
  (let [s (-> (str sym)
              (.replaceAll "\\." "/")
              (.replaceAll "-" "_"))]
    (str s "." (name type))))

(defn resolve-with-ns
  "finds the maven coordinate for a given namespace
   
   (resolve-with-ns '[:clj vinyasa.maven.file]
                    (:dependencies *project*)
                    *project*)
   => '[im.chit/vinyasa.maven \"0.3.1\"]"
  {:added "1.2"}
  [x dependencies project]
  (->> dependencies
       (keep (fn [context]
               (if (maven/resolve-with-dependencies
                    (to-jar-entry x)
                    context
                    :repositories
                    (into {} (:repositories project)))
                 context)))
       first))

(defn find-external-imports
  "finds external imports for a given submodule
   
   (find-external-imports *filemap* *i-deps* \"core\")
   => '#{[:clj vinyasa.maven.file]}"
  {:added "1.2"}
  [filemap i-deps pkg]
  (let [imports     (->> (get filemap pkg)
                         (map :imports)
                         (apply set/union))
        import-deps (->> (get i-deps pkg)
                         (map (fn [dep]
                                (->> (get filemap dep)
                                     (map :exports)
                                     (apply set/union)))))]
    (apply set/difference imports import-deps)))


(defn find-all-external-imports
  "finds external imports for the filemap
   
   (find-all-external-imports *filemap* *i-deps* *project*)
   => {\"web\" #{},
       \"util.data\" #{},
       \"util.array\" #{},
       \"jvm\" #{}
       \"core\" '#{[im.chit/vinyasa.maven \"0.3.1\"]},
       \"common\" #{},
       \"resources\" #{}}"
  {:added "1.2"}
  [filemap i-deps project]
  (reduce-kv (fn [i k v]
               (assoc i k
                      (->> (find-external-imports filemap i-deps k)
                           (map #(resolve-with-ns % (:dependencies project) project))
                           (filter (comp not is-clojure?))
                           (set)
                           (#(disj % nil)))))
             {}
             filemap))
