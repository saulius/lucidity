(ns lucid.aether
  (:require [lucid.aether
             [artifact :as artifact]
             [base :as base]
             [request :as request]
             [result :as result]])
  (:import (org.eclipse.aether.transfer TransferListener)
           (org.eclipse.aether.util.repository AuthenticationBuilder)
           (org.eclipse.aether.repository RemoteRepository$Builder)))

(defn resolve-hierarchy
  {:added "1.1"}
  ([coords]
   (resolve-hierarchy (base/aether) coords))
  ([{:keys [system session]} coords]
   (let [request (request/dependency-request coords)]
     (-> (.resolveDependencies system session request)
         (result/summary)))))

(defn resolve-dependencies
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

(defn install-artifact
  ([coord {:keys [jar-file pom-file] :as opts}]
   (install-artifact (aether) coord opts))
  ([{:keys [system session]} coord opts]
   (let [request (request/install-request coord opts)]
     (-> (.install system session request)
         (result/summary)))))

(defn deploy-artifact
  ([coord {:keys [jar-file pom-file] :as opts}]
   (deploy-artifact (aether) coords opts))
  ([aether coords opts]
   (let [system  (system/repository-system)
         request (request/deploy-request coords opts)
         session (->> (select-keys aether [:local-repo])
                      (session/session system))
         auth (-> (AuthenticationBuilder.)
                  (.addUsername "zcaudate")
                  (.addPassword "password")
                  (.build))
         repo (-> (RemoteRepository$Builder. "clojars"
                                             "default"
                                             "https://clojars.org/repo/")
                  (.setAuthentication auth)
                  (.build))]
     (-> (.deploy system session request)
         (result/summary)))))
