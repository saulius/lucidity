(ns vinyasa.maven
  (:require [clojure.string :as string]
            [hara.reflect :as reflect]
            [vinyasa.maven.jar :as jar]
            [vinyasa.maven.file :as file]
            [wu.kong :as aether])
  (:import [clojure.lang Symbol PersistentVector]))

 (defn maven-coordinate [path & [suffix local-repo]]
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

(defn coordinate-dependencies [coordinates & [repos]]
  (->> coordinates
       (mapcat (fn [coord]
                 (->> (aether/resolve-dependencies coord)
                      (aether/flatten-values))))
       vec))

(defn resolve-jar
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
  [x & more] (if-let [path (-> (apply resolve-jar x more)
                               (first))]
               (maven-coordinate path)))

(defn resolve-with-deps
  ([x] (resolve-with-deps x nil))
  ([x context & {:keys [repositories] :as options}]
     (cond (nil? context)
           (resolve-with-deps x (-> x jar/resolve-jar first))

           (string? context)
           (apply resolve-with-deps x (maven-coordinate context) options)

           (vector? context)
           (condp = (type (first context))
              String
              (apply resolve-with-deps x (map maven-coordinate context) options)

              Symbol (jar/resolve-jar x :coordinates
                                      (coordinate-dependencies [context] repositories))
              PersistentVector
              (jar/resolve-jar x :coordinates
                               (coordinate-dependencies context repositories))))))

(def add-url
  (reflect/query-class java.net.URLClassLoader ["addURL" :#]))

(defn pull [coord]
  (let [deps (-> (aether/resolve-dependencies coord)
                 (aether/flatten-values))]
    (doseq [dep deps]
      (add-url (.getClassLoader clojure.lang.RT)
               (java.net.URL. (str "file:" (jar/maven-file dep)))))))
