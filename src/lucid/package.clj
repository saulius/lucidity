(ns lucid.package
  (:require [clojure.string :as string]
            [clojure.set :as set]
            [hara.io.classpath :as classpath]
            [hara.namespace.import :as ns]
            [hara.reflect :as reflect]
            [lucid.package
             [jar :as jar]
             [pom :as pom]]
            [lucid.aether :as aether])
  (:import [clojure.lang Symbol PersistentVector]))

(ns/import lucid.package.pom [generate-pom]
           lucid.package.jar [generate-jar generate-manifest])

(defn coordinate-dependencies
  "list dependencies for a coordinate
 
   (coordinate-dependencies [['org.clojure/core.match *match-version*]])
   => []"
  {:added "1.1"}
  [coordinates & [repos]]
  (->> coordinates
       (map (fn [coord]
              (aether/resolve-dependencies coord)))
       (apply set/union)
       vec))

(defn resolve-with-dependencies
  "resolves the jar and path of a namespace
 
   (resolve-with-dependencies 'clojure.core.match)
   => [*match-path* \"clojure/core/match.clj\"]"
  {:added "1.1"}
  ([x] (resolve-with-dependencies x nil))
  ([x context & {:keys [repositories] :as options}]
   (cond (nil? context)
         (resolve-with-dependencies x (-> x jar/resolve-jar first))

         (string? context)
         (apply resolve-with-dependencies x (coordinate context) options)

         (vector? context)
         (condp = (type (first context))
            String
            (apply resolve-with-dependencies x (map coordinate context) options)

            Symbol (jar/resolve-jar x :coordinates
                                    (coordinate-dependencies [context] repositories))
            PersistentVector
            (jar/resolve-jar x :coordinates
                             (coordinate-dependencies context repositories)))

         :else (do (throw (Exception. (str x ":" context)))))))

(def add-url
  (reflect/query-class java.net.URLClassLoader ["addURL" :#]))

(defn pull
  "pulls down the necessary dependencies from maven and adds it to the project
 
   (pull ['org.clojure/core.match *match-version*])
   => '[[org.ow2.asm/asm-all \"4.1\"]
        [org.clojure/tools.analyzer.jvm \"0.1.0-beta12\"]
        [org.clojure/tools.analyzer \"0.1.0-beta12\"]
        [org.clojure/data.priority-map \"0.0.2\"]
        [org.clojure/core.memoize \"0.5.6\"]
       [org.clojure/core.match \"0.2.2\"]
        [org.clojure/core.cache \"0.6.3\"]]"
  {:added "1.1"}
  [coord]
  (let [deps (aether/resolve-with-dependencies coord)]
    (doseq [dep deps]
      (add-url (.getClassLoader clojure.lang.RT)
               (java.net.URL. (str "file:" (jar/maven-file dep)))))
    deps))
