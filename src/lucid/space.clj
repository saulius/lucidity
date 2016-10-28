(ns lucid.space
  (:require [clojure.string :as string]
            [clojure.set :as set]
            [hara.namespace.import :as ns]
            [hara.reflect :as reflect]
            [lucid.space.jar :as jar]
            [lucid.space.file :as file]
            [lucid.space.search :as search]
            [lucid.core.aether :as aether])
  (:import [clojure.lang Symbol PersistentVector]))

(ns/import lucid.space.jar [maven-file jar-entry]
           lucid.space.search [all-jars search])

(defn coordinate
  "creates a coordinate based on the path
 
   (coordinate *match-path*)
   => ['org.clojure/core.match *match-version*]"
  {:added "1.1"}
  [path & [suffix local-repo]]
  (if (and (.startsWith path (or local-repo jar/*local-repo*))
           (.endsWith   path (or suffix ".jar")))
    (let [[_ version artifact & group]
          (-> (subs path (count (or local-repo jar/*local-repo*)))
              (clojure.string/split (re-pattern file/*sep*))
              (->> (filter (comp not empty?)))
              (reverse))]
      (-> (clojure.string/join  "." (reverse group))
          (str file/*sep* artifact)
          symbol
          (vector version)))
    (throw (Exception. (str "The path " path " does not conform to a valid maven repo jar")))))

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

(defn resolve-jar
  "resolves a jar according to context
 
   (resolve-jar 'clojure.core.match)
   => [*match-path* \"clojure/core/match.clj\"]"
  {:added "1.1"}
  ([x] (jar/resolve-jar x nil))
  ([x context & args]
   (cond (keyword? context)
         (apply jar/resolve-jar x context args)

         (string? context)
         (jar/resolve-jar x :jar-path context)

         (instance? ClassLoader context)
         (jar/resolve-jar x :classloader context)

         (vector? context)
         (condp = (type (first context))
           String (jar/resolve-jar x :jar-paths context)
           Symbol (jar/resolve-jar x :coordinate context)
           PersistentVector (jar/resolve-jar x :coordinates context)))))

(defn resolve-coordinates
  "resolves a set of coordinates
 
   (resolve-coordinates 'clojure.core.match)
   => ['org.clojure/core.match *match-version*]"
  {:added "1.1"}
  [x & more]
  (if-let [path (-> (apply resolve-jar x more)
                    (first))]
    (coordinate path)))

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
  (let [deps (aether/resolve-dependencies coord)]
    (doseq [dep deps]
      (add-url (.getClassLoader clojure.lang.RT)
               (java.net.URL. (str "file:" (jar/maven-file dep)))))
    deps))
