(ns documentation.lucid-space
  (:use hara.test)
  (:require [lucid.space :refer :all]))

[[:chapter {:title "Introduction"}]]

"`lucid.space` allows for introspection of java libraries through maven. The library provides mappings for better understanding where a class, file or namespace is located on the filesystem. This library was originally [korra](https://github.com/zcaudate/hara/korra) and then [vinyasa.maven](https://github.com/zcaudate/maven)."

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [tahto/lucid.space "{{PROJECT.version}}"])

"All functionality is in the `lucid.space` namespace:"

(comment
  (use 'lucid.space))
  
[[:section {:title "Motivation"}]]

"There is a reversible mapping between the maven jar file and the `:dependencies` for a library specified in `project.clj`. We can map between a maven coordinate and it's jar file though utility functions. We can also map a 'resource' and its related jar and jar entry under a given context.

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
  - the entire maven local-repo."

[[:chapter {:title "Pull"}]]

"This is one of the most useful functions for exploring any clojure library. How many times have you forgotten a library dependency for `project.clj` and then had to restart your nrepl? `pull` takes care of the dependencies for the project:"

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

[[:chapter {:title "Jar File"}]]

[[:section {:title "maven-file"}]]

"We use `maven-file` to convert a coordinate into a path to a file"

(comment
  (maven-file '[org.clojure/clojure "1.6.0"])
  => "../org/clojure/clojure/1.6.0/clojure-1.6.0.jar")

[[:section {:title "coordinate"}]]

"We use `coordinate` to convert the path back into a coordinate"

(comment
  (coordinate "../org/clojure/clojure/1.6.0/clojure-1.6.0.jar")
  => '[org.clojure/clojure "1.6.0"])

[[:section {:title "jar-entry"}]]

"There is also a mapping between a clojure namespace, a java class and the their location in a jar. This is accessible through `jar-entry`:"

(comment
  (jar-entry (maven-file '[org.clojure/clojure "1.6.0"])
             'clojure.core)
  ;;  #object[java.util.jar.JarFile$JarFileEntry 0x29a1bd87 "clojure/core.clj"]
  
  (jar-entry (maven-file '[org.clojure/clojure "1.6.0"])
             'clojure.core.match)
  => nil)


[[:section {:title "coordinate-dependencies"}]]

"To find all coordinate dependencies associated with a given library use the aptly named `coordinate-dependencies`:"

(comment
  (coordinate-dependencies '[[im.chit/hara.event "2.4.4"]])
  => '[[im.chit/hara.data.seq "2.4.4"]
       [im.chit/hara.common.checks "2.4.4"]
       [im.chit/hara.event "2.4.4"]
       [im.chit/hara.common.error "2.4.4"]
       [im.chit/hara.data.map "2.4.4"]
       [im.chit/hara.common.primitives "2.4.4"]])

(comment
  (coordinate-dependencies '[[org.clojure/clojure "1.8.0"]])
  => '[[org.clojure/clojure "1.8.0"]])


[[:chapter {:title "Resolution"}]]

[[:section {:title "resolve-jar"}]]

"Understanding where a file or namespace comes from is really important because it allows understanding of the structure of the libraries and how they fit together. The main workhorse for file resolution is `resolve-jar`. It resolves a `resource` and a `context`. The default context is the current jvm classpath and can be omitted by default:"

(comment
  (resolve-jar 'clojure.core)
  => ["../org/clojure/clojure/1.8.0/clojure-1.8.0.jar"
      "clojure/core.clj"])

"It will resolve classes:"

(comment
  (resolve-jar java.lang.Object)
  => ["../jdk1.8.0_102.jdk/Contents/Home/jre/lib/rt.jar"
      "java/lang/Object.class"])

"It will also resolve paths to files:"

(comment
  (resolve-jar "clojure/core.clj")
  => ["../org/clojure/clojure/1.8.0/clojure-1.8.0.jar"
      "clojure/core.clj"])

"Symbols with the last section capitalized will default to java classes instead of clojure files:"

(comment
  (resolve-jar 'clojure.lang.IProxy)
  => ["../org/clojure/clojure/1.8.0/clojure-1.8.0.jar"
      "clojure/lang/IProxy.class"])

"It will return nil if the resource cannot be found:"

(comment
  (resolve-jar 'does.not.exist)
  => nil)

[[:section {:title "contexts"}]]

"Apart from searching via the current jvm classpath, other search contexts can be set, the most simple being a string representation of the jar path:"

(comment
  (resolve-jar 'clojure.core
               "../org/clojure/clojure/1.6.0/clojure-1.6.0.jar")
  => ["../org/clojure/clojure/1.6.0/clojure-1.6.0.jar"
      "clojure/core.clj"])


"If the entry cannot be found, nil will be returned. In this case, we know that `clojure.core` is not in the dynapath jar:"

(comment
  (resolve-jar 'clojure.core
               "../dynapath/dynapath/0.2.0/dynapath-0.2.0.jar")
  => nil)

"In addition to the jar, one can use as the contexte a vector of jar-files:"

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

[[:section {:title "resolve-coordinates"}]]

"`resolve-coordinates` works similarly to `resolve-jar` but will return the actual maven-style coordinates"

(comment
  (resolve-coordinates 'version-clj.core)
  => '[version-clj/version-clj "0.1.2"]
  
  (resolve-coordinates 'clojure.core :repository)
  => '[org.clojure/clojure "1.8.0"])

[[:section {:title "resolve-with-deps"}]]

"`resolve-with-deps` will recursively search all child dependencies until it finds the file:"

(comment
  (resolve-with-deps 'hara.test
                     '[[im.chit/hara "2.4.4"]])
  => ["../im/chit/hara.test/2.4.4/hara.test-2.4.4.jar"
      "hara/test.clj"])


