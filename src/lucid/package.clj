(ns lucid.package
  (:require [hara.namespace.import :as ns]
            [hara.io.project :as project]
            [lucid.aether :as aether]
            [lucid.package
             [jar :as jar]
             [pom :as pom]
             [privacy :as privacy]
             [resolve :as resolve]
             [user :as user]]))

(ns/import
 
 lucid.package.pom
 [generate-pom]

 lucid.package.jar
 [generate-jar]

 lucid.package.resolve
 [list-dependencies
  pull
  resolve-with-dependencies])

(defn install-project
  [project]
  (let [jar-file (jar/generate-jar project)
        pom-file (pom/generate-pom project)]
    (-> project
        (select-keys [:group :artifact :version])
        (aether/install-artifact {:artifacts [{:file jar-file
                                               :extension "jar"}
                                              {:file pom-file
                                               :extension "pom"}]}))))

(defn sign-file
  [{:keys [file extension]}
   {:keys [signing suffix ring-file]
    :or {suffix "asc"
         ring-file user/GNUPG-SECRET}}]
  (let [output (str file "." suffix)
        output-ex (str extension "." suffix)]
    (privacy/sign file output ring-file signing)
    {:file output
     :extension output-ex}))

(defn add-authentication
  [{:keys [id] :as repository} {:keys [ring-file cred-file]
       :or {cred-file user/LEIN-CREDENTIALS-GPG
            ring-file user/GNUPG-SECRET}}]
  (let [auth-map (read-string (privacy/decrypt cred-file ring-file))]
    (->> auth-map
         (filter (fn [[k _]]
                   (cond (string? k)
                         (= id k)
                         
                         (instance? java.util.regex.Pattern k)
                         (re-find k id))))
         first
         second
         (assoc repository :authentication))))

(defn deploy-project
  ([project]
   (deploy-project project {}))
  ([project {:keys [id]
             :or {id "clojars"}}]
   (let [jar-file (jar/generate-jar project)
         pom-file (pom/generate-pom project)
         aether   (aether/aether)
         repository (->> (:repositories aether)
                         (filter #(-> % :id (= id)))
                         first)
         repository (add-authentication repository {})
         artifacts  [{:file jar-file
                      :extension "jar"}
                     {:file pom-file
                      :extension "pom"}]
         signing  (-> user/LEIN-PROFILE
                      slurp
                      read-string
                      (get-in [:user :signing :gpg-key]))
         signed  (if signing
                   (map sign-file artifacts))]
     (-> project
         (select-keys [:group :artifact :version])
         (aether/deploy-artifact aether
                                 {:artifacts (concat artifacts signed)
                                  :repository repository})))))
