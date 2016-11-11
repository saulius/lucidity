(ns lucid.aether.base
  (:require [lucid.aether
             [session :as session]
             [system :as system]]))

(defonce +defaults+
  {:repositories [{:id "clojars"
                   :name "clojars"
                   :type "default"
                   :url "http://clojars.org/repo"}
                  {:id "central"
                   :name "central"
                   :type "default"
                   :url "http://central.maven.org/maven2/"}]})

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
  ([config]
   (let [system  (system/repository-system)
         session (->> (select-keys aether [:local-repo])
                      (session/session system))]
     (map->Aether {:system system
                   :session session}))))
