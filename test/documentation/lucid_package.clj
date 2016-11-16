(ns documentation.lucid-package
  (:use hara.test)
  (:require [lucid.package :refer :all]))

[[:chapter {:title "Introduction"}]]

"`lucid.package` allows for  of the project, either through installlation, deployment or dependency management"

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [im.chit/lucid.package "{{PROJECT.version}}"])

"All functionality is in the `lucid.package` namespace:"

(comment
  (use 'lucid.package))
  
[[:chapter {:title "Index"}]]

[[:api {:title ""
        :namespace "lucid.package"
        :display #{:tags}
        :exclude ["add-authentication"
                  "sign-file"]}]]

[[:chapter {:title "API"}]]

[[:section {:title "Dependencies"}]]

[[:api {:title ""
        :namespace "lucid.package"
        :only ["list-dependencies"
               "resolve-with-dependencies"]}]]

[[:section {:title "Pull"}]]

[[:api {:title ""
        :namespace "lucid.package"
        :only ["pull"]}]]

"`pull` is one of the most useful functions for exploring any clojure library. How many times have you forgotten a library dependency for `project.clj` and then had to restart your nrepl? `pull` takes care of the dependencies for the project:"

(comment
  (require 'schema.core)
  ;; java.io.FileNotFoundException: Could not locate hiccup/core__init.class or hiccup/core.clj on classpath:
  
  (pull '[prismatic/schema "1.1.3"])
  => '[[prismatic/schema "1.1.3"]]
  
  (use 'schema.core)
  
  (validate Num "hello")
  => (throws))

[[:section {:title "Project"}]]

[[:api {:title ""
        :namespace "lucid.package"
        :only ["compile-project"
               "deploy-project"
               "install-project"
               "generate-jar"
               "generate-pom"]}]]
