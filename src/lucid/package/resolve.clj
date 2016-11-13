(ns lucid.package.resolve
  (:require [lucid.aether :as aether]
            [hara.io.classpath :as classpath]
            [hara.reflect :as reflect]
            [clojure.set :as set]
            [clojure.string :as string])
  (:import [clojure.lang Symbol IPersistentVector]))

(defn list-dependencies
  "list the dependencies for a particular coordinate
   
   (list-dependencies '[[im.chit/hara.test \"2.4.8\"]])
   => '[[im.chit/hara.class.enum \"2.4.8\"]
        [im.chit/hara.class.inheritance \"2.4.8\"]
        [im.chit/hara.common.checks \"2.4.8\"]
        [im.chit/hara.common.error \"2.4.8\"]
        [im.chit/hara.common.primitives \"2.4.8\"]
        [im.chit/hara.common.string \"2.4.8\"]
        [im.chit/hara.data.map \"2.4.8\"]
       [im.chit/hara.data.seq \"2.4.8\"]
        [im.chit/hara.event \"2.4.8\"]
        [im.chit/hara.io.ansii \"2.4.8\"]
        [im.chit/hara.io.file \"2.4.8\"]
        [im.chit/hara.io.project \"2.4.8\"]
        [im.chit/hara.namespace.import \"2.4.8\"]
        [im.chit/hara.protocol.string \"2.4.8\"]
        [im.chit/hara.string.case \"2.4.8\"]
        [im.chit/hara.test \"2.4.8\"]]"
  {:added "1.2"}
  ([coordinates]
   (list-dependencies coordinates {}))
  ([coordinates {:keys [repositories] :as opts}]
   (->> coordinates
        (map (fn [coord]
               (-> (aether/aether opts)
                   (aether/resolve-dependencies coord))))
        (apply set/union)
        sort
        vec)))

(defn resolve-with-dependencies
  "resolves an entry with all artifact dependencies
 
   (resolve-with-dependencies 'hara.data.map
                              '[im.chit/hara.test \"2.4.8\"])
   => '[[im.chit/hara.data.map \"2.4.8\"]
        \"hara/data/map.clj\"]"
  {:added "1.2"}
  ([x context] (resolve-with-dependencies x context {}))
  ([x context {:keys [repositories] :as opts}]
   (let [resolve-entry (fn [x context]
                         (classpath/resolve-entry x context {:tag :coord}))]
     (cond (string? context)
           (->> (list-dependencies [context] opts)
                (resolve-entry x))

           (vector? context)
           (let [n (first context)]
             (cond (or (string? n)
                       (instance? IPersistentVector n))
                   (resolve-entry x context)

                   (symbol? n)
                   (->> (list-dependencies [context] repositories)
                        (resolve-entry x))))

           :else
           (throw (Exception. (str "Not supported: " x " " context)))))))

(def add-url
  (reflect/query-class java.net.URLClassLoader ["addURL" :#]))

(defn pull
  "pulls down the necessary dependencies from maven and adds it to the project
 
   (pull '[org.clojure/core.match \"0.2.2\"])
   => '[[org.clojure/core.cache \"0.6.3\"]
        [org.clojure/core.match \"0.2.2\"]
        [org.clojure/core.memoize \"0.5.6\"]
        [org.clojure/data.priority-map \"0.0.2\"]
        [org.clojure/tools.analyzer \"0.1.0-beta12\"]
       [org.clojure/tools.analyzer.jvm \"0.1.0-beta12\"]
        [org.ow2.asm/asm-all \"4.1\"]]"
  {:added "1.1"}
  [coord]
  (let [deps (list-dependencies [coord])]
    (doseq [dep deps]
      (add-url (.getClassLoader clojure.lang.RT)
               (java.net.URL. (str "file:" (classpath/artifact :path dep)))))
    deps))
