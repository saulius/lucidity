(ns lucid.package.pom
  (:require [clojure.string :as string]
            [hiccup.core :as html]
            [hara.io
             [file :as fs]
             [project :as project]]))

(def HEADER
  {"xmlns"
   "http://maven.apache.org/POM/4.0.0"

   "xmlns:xsi"
   "http://www.w3.org/2001/XMLSchema-instance"

   "xsi:schemaLocation"
   (str "http://maven.apache.org/POM/4.0.0"
        " "
        "http://maven.apache.org/xsd/maven-4.0.0.xsd")})

(defn pom-properties
  "creates a pom.properties file
 
   (pom-properties (project/project))"
  {:added "1.2"}
  [project]
  (str "# lucid.distribute\n"
       "# " (java.util.Date.) "\n"
       "version=" (:version project) "\n"
       "groupId=" (:group project) "\n"
       "artifactId=" (:artifact project)))

(defn coordinate->dependency
  "creates a hiccup dependency entry
 
   (coordinate->dependency '[im.chit/hara \"0.1.1\"])
   => [:dependency
       [:groupId \"im.chit\"]
       [:artifactId \"hara\"]
       [:version \"0.1.1\"]]"
  {:added "1.2"}
  [[full version]]
  (let [group (or (namespace full)
                  (str full))
        artifact (name full)]
    [:dependency
     [:groupId group]
     [:artifactId artifact]
     [:version version]]))

(defn pom-xml
  "creates a pom.properties file
 
   (pom-xml (project/project))"
  {:added "1.2"}
  [project]
  (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
       "\n"
       (html/html
        [:project HEADER
         [:modelVersion "4.0.0"]
         [:packaging "jar"]
         [:groupId (:group project)]
         [:artifactId (:artifact project)]
         [:version (:version project)]
         [:name (str (:name project))]
         [:description (:description project)]
         [:url (:url project)]
         [:licenses
          [:license
           [:name (-> project :license :name)]
           [:url (-> project :license :url)]]]
         [:repositories
          [:repository
           [:id "central"]
           [:url "https://repo1.maven.org/maven2/"]
           [:snapshots [:enabled "false"]]
           [:releases [:enabled "true"]]]
          [:repository
           [:id "clojars"]
           [:url "https://clojars.org/repo"]
           [:snapshots [:enabled "true"]]
           [:releases [:enabled "false"]]]]
         (apply vector :dependencies
                (map coordinate->dependency (:dependencies project)))])))

(defn generate-pom
  "generates all the pom information for the project
 
   (pom-xml (project/project))"
  {:added "1.2"}
  [project]
  (let [root (:root project)
        output (str "target/classes/META-INF/maven/"
                    (:group project)
                    "/"
                    (:artifact project))
        pom-path (str root "/target/" (:artifact project) "-" (:version project) ".pom.xml")
        xml    (pom-xml project)]
    (fs/create-directory (fs/path root output))
    (spit (str (fs/path root output "pom.xml")) xml)
    (spit (str (fs/path root output "pom.properties")) (pom-properties project))
    (spit pom-path xml)
    pom-path))



