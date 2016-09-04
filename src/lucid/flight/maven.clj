(ns lucid.flight.maven
  (:require [clojure.string :as string]
            [hara.reflect :as reflect]
            [lucid.flight.maven.jar :as jar]
            [lucid.flight.maven.file :as file]
            [wu.kong :as aether])
  (:import [clojure.lang Symbol PersistentVector]))

(defn coordinate
  "creates a coordinate based on the path
 
   (coordinate *hara-test-path*)
   => ['im.chit/hara.test *hara-version*]"
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
 
   (coordinate-dependencies '[[im.chit/hara.test \"2.4.1\"]])
   => (contains '[[im.chit/hara.test \"2.4.1\"]
                  [im.chit/hara.namespace.import \"2.4.1\"]
                  [im.chit/hara.event \"2.4.1\"]
                  [im.chit/hara.common.primitives \"2.4.1\"]
                  [im.chit/hara.data.seq \"2.4.1\"]
                 [im.chit/hara.data.map \"2.4.1\"]
                  [im.chit/hara.common.checks \"2.4.1\"]
                  [im.chit/hara.common.primitives \"2.4.1\"]
                  [im.chit/hara.common.error \"2.4.1\"]
                  [im.chit/hara.common.checks \"2.4.1\"]
                  [im.chit/hara.io.file \"2.4.1\"]
                  [im.chit/hara.display.ansii \"2.4.1\"]]
                :in-any-order)"
  {:added "1.1"}
  [coordinates & [repos]]
  (->> coordinates
       (mapcat (fn [coord]
                 (->> (aether/resolve-dependencies coord)
                      (aether/flatten-values))))
       vec))

(defn resolve-jar
  "resolves a jar according to context
 
   (resolve-jar 'hara.test)
   => [*hara-test-path* \"hara/test.clj\"]"
  {:added "1.1"}
  ([x] (jar/resolve-jar x nil))
  ([x context & args]
   (cond (keyword? context)
         (apply jar/resolve-jar x context args)

         (string? context)
         (jar/resolve-jar x :jar-path (munge context))

         (instance? ClassLoader context)
         (jar/resolve-jar x :classloader context)

         (vector? context)
         (condp = (type (first context))
           String (jar/resolve-jar x :jar-paths context)
           Symbol (jar/resolve-jar x :coordinate context)
           PersistentVector (jar/resolve-jar x :coordinates context)))))

(defn resolve-coordinates
  "resolves a set of coordinates
 
   (resolve-coordinates 'hara.test)
   => ['im.chit/hara.test *hara-version*]"
  {:added "1.1"}
  [x & more]
  (if-let [path (-> (apply resolve-jar x more)
                    (first))]
    (coordinate path)))

(defn resolve-with-deps
  "resolves the jar and path of a namespace
 
   (resolve-with-deps 'hara.test)
   => [*hara-test-path* \"hara/test.clj\"]
   "
  {:added "1.1"}
  ([x] (resolve-with-deps x nil))
  ([x context & {:keys [repositories] :as options}]
   (cond (nil? context)
         (resolve-with-deps x (-> x jar/resolve-jar first))

         (string? context)
         (apply resolve-with-deps x (coordinate context) options)

         (vector? context)
         (condp = (type (first context))
            String
            (apply resolve-with-deps x (map coordinate context) options)

            Symbol (jar/resolve-jar x :coordinates
                                    (coordinate-dependencies [context] repositories))
            PersistentVector
            (jar/resolve-jar x :coordinates
                             (coordinate-dependencies context repositories))))))

(def add-url
  (reflect/query-class java.net.URLClassLoader ["addURL" :#]))

(defn pull
  "pulls down the necessary dependencies from maven and adds it to the project
 
   (pull '[im.chit/hara.test \"2.4.1\"])
   => (contains '[[im.chit/hara.test \"2.4.1\"]
                  [im.chit/hara.namespace.import \"2.4.1\"]
                  [im.chit/hara.event \"2.4.1\"]
                  [im.chit/hara.common.primitives \"2.4.1\"]
                  [im.chit/hara.data.seq \"2.4.1\"]
                 [im.chit/hara.data.map \"2.4.1\"]
                  [im.chit/hara.common.checks \"2.4.1\"]
                  [im.chit/hara.common.primitives \"2.4.1\"]
                  [im.chit/hara.common.error \"2.4.1\"]
                  [im.chit/hara.common.checks \"2.4.1\"]
                  [im.chit/hara.io.file \"2.4.1\"]
                  [im.chit/hara.display.ansii \"2.4.1\"]]
                :in-any-order)"
  {:added "1.1"}
  [coord]
  (let [deps (-> (aether/resolve-dependencies coord)
                 (aether/flatten-values))]
    (doseq [dep deps]
      (add-url (.getClassLoader clojure.lang.RT)
               (java.net.URL. (str "file:" (jar/maven-file dep)))))
    deps))
