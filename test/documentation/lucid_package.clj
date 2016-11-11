(ns documentation.lucid-package
  (:use hara.test)
  (:require [lucid.package :refer :all]))

[[:chapter {:title "Introduction"}]]

"`lucid.package` allows for introspection of java libraries through maven. The library provides mappings for better understanding where a class, file or namespace is located on the filesystem. This library was originally [korra](https://github.com/zcaudate/hara/korra) and then [vinyasa.maven](https://github.com/zcaudate/maven)."

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
        :display #{:tags}}]]

[[:chapter {:title "API"}]]

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
  
  (validate Num 3)
  => 3
  
  (validate Num "hello")
  => (throws))

[[:section {:title "Search"}]]

[[:api {:title ""
        :namespace "lucid.package"
        :only ["all-jars" "search"]}]]

[[:section {:title "Jars"}]]

[[:api {:title ""
        :namespace "lucid.package"
        :only ["coordinate" "coordinate-dependencies"
               "jar-entry" "maven-file"]}]]

[[:section {:title "Resolution"}]]

[[:api {:title ""
        :namespace "lucid.package"
        :only ["resolve-coordinates"
               "resolve-jar"
               "resolve-with-dependencies"]}]]

[[:section {:title "Contexts"}]]

"We can also associate a 'resource' and its related jar and jar entry under a given context:

- the resource can be:
  - a symbol representing a clojure namespace
  - a path to a resource
  - a java class
- the context can be:
  - the jvm classloader classpath
  - a single jar
  - a list of jars
  - a maven coordinate
  - a list of maven coordinates
  - the entire maven local-repo.

The default context is a search via the current jvm classpath. However, contexts can be set, the most simple being the jar path:"

(comment
  (resolve-jar 'clojure.core
               "../org/clojure/clojure/1.6.0/clojure-1.6.0.jar")
  => ["../org/clojure/clojure/1.6.0/clojure-1.6.0.jar"
      "clojure/core.clj"])


"If the entry cannot be found, `nil` will be returned. In this case, we know that `clojure.core` is not in the dynapath jar:"

(comment
  (resolve-jar 'clojure.core
               "../dynapath/dynapath/0.2.0/dynapath-0.2.0.jar")
  => nil)

"In addition to the jar, one can use a vector of jar-files:"

(comment
  (resolve-jar 'clojure.core
               ["../org/clojure/clojure/1.6.0/clojure-1.6.0.jar"
                "../dynapath/dynapath/0.2.0/dynapath-0.2.0.jar"])
  => ["../org/clojure/clojure/1.6.0/clojure-1.6.0.jar"
      "clojure/core.clj"])

"or a coordinate:"

(comment
  (resolve-jar 'clojure.core '[org.clojure/clojure "1.6.0"])
  => ["../org/clojure/clojure/1.6.0/clojure-1.6.0.jar"
      "clojure/core.clj"])

"or a vector of coordinates:"

(comment
  (resolve-jar 'clojure.core '[[org.clojure/clojure "1.6.0"]
                               [dynapath "0.2.0"]])
  => ["../org/clojure/clojure/1.6.0/clojure-1.6.0.jar"
      "clojure/core.clj"])


"or if you simply just want to explore, the context can be an entire maven local repository:"

(comment
  (resolve-jar 'clojure.core :repository)
  => ["../org/clojure/clojure/1.8.0/clojure-1.8.0.jar"
      "clojure/core.clj"]
  
  (resolve-jar 'dynapath.util :repository)
  => ["../org/tcrawley/dynapath/0.2.4/dynapath-0.2.4.jar"
      "dynapath/util.clj"])
