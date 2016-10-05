(ns lucid.core.aether
  (:require [lucid.core.aether
             [artifact :as artifact]
             [local-repo :as local]
             [session :as session]
             [system :as system]
             [request :as request]
             [result :as result]]))

(defonce +defaults+
  {:repositories {"clojars" {:name "clojars"
                             :url "http://clojars.org/repo"}
                  "central" {:name "central"
                             :url "http://central.maven.org/maven2/"}}})

(defrecord Aether [])

(defmethod print-method Aether
  [v w]
  (.write w (str "#aether" (into {} v))))

(defn aether
  "creates an `Aether` object
 
   (into {} (aether))
   => +defaults+"
  {:added "1.1"}
  ([] (map->Aether +defaults+))
  ([config] (map->Aether ())))

(defn flatten-values
  ""
  [node]
  (cond (map? node)
        (cons (key (first node))
                    (flatten-values (val (first node))))
        
        (vector? node)
        (mapcat (fn [item]
                  (cond (map? item)
                        (flatten-values item)

                        (vector? item)
                        [item]))
                node)))

(defn resolve-hierarchy
  " shows the dependency hierachy for all packages
   
   (resolve-hierarchy '[midje \"1.6.3\"])
   => '{[midje/midje \"1.6.3\"]
        [{[ordered/ordered \"1.2.0\"] []}
         {[org.clojure/math.combinatorics \"0.0.7\"] []}
         {[org.clojure/core.unify \"0.5.2\"] []}
         {[utilize/utilize \"0.2.3\"]
          [{[org.clojure/tools.macro \"0.1.1\"] []}
           {[joda-time/joda-time \"2.0\"] []}
          {[ordered/ordered \"1.0.0\"] []}]}
         {[colorize/colorize \"0.1.1\"] []}
         {[org.clojure/tools.macro \"0.1.5\"] []}
         {[dynapath/dynapath \"0.2.0\"] []}
         {[swiss-arrows/swiss-arrows \"1.0.0\"] []}
         {[org.clojure/tools.namespace \"0.2.4\"] []}
         {[slingshot/slingshot \"0.10.3\"] []}
         {[commons-codec/commons-codec \"1.9\"] []}
         {[gui-diff/gui-diff \"0.5.0\"]
          [{[org.clojars.trptcolin/sjacket \"0.1.3\"]
            [{[net.cgrand/regex \"1.1.0\"] []}
             {[net.cgrand/parsley \"0.9.1\"]
              [{[net.cgrand/regex \"1.1.0\"] []}]}]}
           {[ordered/ordered \"1.2.0\"] []}]}
         {[clj-time/clj-time \"0.6.0\"]
          [{[joda-time/joda-time \"2.2\"] []}]}]}"
  {:added "1.1"}
  ([coords]
   (resolve-hierarchy (aether) coords))
  ([aether coords]
   (let [system  (system/repository-system)
         request (request/dependency-request aether coords)
         session (->> (select-keys aether [:local-repo])
                      (session/session system))]
     (-> (.resolveDependencies system session request)
         (result/summary)))))

(defn resolve-dependencies
  "resolves maven dependencies for a set of coordinates
 
   (resolve-dependencies '[prismatic/schema \"1.1.3\"])
   => '[[prismatic/schema \"1.1.3\"]]
   
   (resolve-dependencies '[midje \"1.6.3\"])
   => '[[utilize/utilize \"0.2.3\"]
        [swiss-arrows/swiss-arrows \"1.0.0\"]
        [slingshot/slingshot \"0.10.3\"]
        [org.clojure/tools.namespace \"0.2.4\"]
        [org.clojure/tools.macro \"0.1.5\"]
        [org.clojure/math.combinatorics \"0.0.7\"]
        [org.clojure/core.unify \"0.5.2\"]
        [org.clojars.trptcolin/sjacket \"0.1.3\"]
        [ordered/ordered \"1.2.0\"]
        [net.cgrand/regex \"1.1.0\"]
        [net.cgrand/parsley \"0.9.1\"]
        [midje/midje \"1.6.3\"]
       [joda-time/joda-time \"2.2\"]
        [gui-diff/gui-diff \"0.5.0\"]
        [dynapath/dynapath \"0.2.0\"]
        [commons-codec/commons-codec \"1.9\"]
        [colorize/colorize \"0.1.1\"]
        [clj-time/clj-time \"0.6.0\"]]"
  {:added "1.1"}
  ([coords]
   (resolve-dependencies (aether) coords))
  ([aether coords]
   (->> (resolve-hierarchy aether coords)
        (flatten-values)
        (sort)
        (reverse)
        (reduce (fn [out coord]
                  (if (-> out last first (= (first coord)))
                    out
                    (conj out coord)))
                []))))
