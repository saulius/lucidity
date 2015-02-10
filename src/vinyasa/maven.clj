(ns vinyasa.maven
  (:require [clojure.string :as string]
            [vinyasa.maven.jar :as jar]
            [vinyasa.maven.file :as file]
            [cemerick.pomegranate.aether :as aether])
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
  (->> (aether/resolve-dependencies
        :coordinates coordinates
        :repositories (merge {"clojars" "http://clojars.org/repo"
                              "central" "http://repo1.maven.org/maven2/"}
                             repos))
       (map #(take 2 (first %)))
       (mapv vec)))

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
