(ns documentation.lucid-core
  (:use hara.test)
  (:require [lucid.core
             [asm :refer :all]
             [debug :refer :all]
             [inject :refer :all :as inject]
             [namespace :refer :all]]))

"`lucid.core` provides utilities that either support the rest of the `lucidity` suite or are useful standalone tools by themselves. Each one is installed individually and usually only provides one or two top level function for use:" 

[[:chapter {:title "core.asm"
            :link "lucid.core.asm"
            :only ["dynamic-loader"
                   "load-class"
                   "to-bytes"
                   "unload-class"]}]]

"`lucid.core.asm` allows exploration of classes on the filesystem, independent of the JVM classloader."

"Add to `project.clj`:

    [im.chit/lucid.core.asm \"{{PROJECT.version}}\"]

All functionality is in the `lucid.core.asm` namespace:"

(comment
  (use 'lucid.core.asm))

[[:api {:namespace "lucid.core.asm"
        :title ""
        :only ["dynamic-loader"
               "load-class"
               "to-bytes"
               "unload-class"]}]]

[[:chapter {:title "core.code"
            :link "lucid.core.code"
            :only ["analyse-file"]}]]

"`lucid.core.code` is analyses source and test code and is used by `lucid.publish` and `lucid.unit` to build additional functionality."

"Add to `project.clj`:

    [im.chit/lucid.core.code \"{{PROJECT.version}}\"]

All functionality is in the `lucid.core.code` namespace:"

(comment
  (use 'lucid.core.code))

[[:api {:namespace "lucid.core.code"
        :only ["analyse-file"]
        :title ""}]]

[[:chapter {:title "core.debug"
            :link "lucid.core.debug"
            :only ["dbg->" "dbg->>" "->doto" "->>doto" "->prn"]}]]

"`lucid.core.debug` contains macros and helpers for debugging"

"Add to `project.clj`:

    [im.chit/lucid.core.debug \"{{PROJECT.version}}\"]

All functionality is in the `lucid.core.debug` namespace:"

(comment
  (use 'lucid.core.debug))

[[:api {:namespace "lucid.core.debug"
        :title ""
        :only ["dbg->" "dbg->>" "->doto" "->>doto" "->prn"]}]]

[[:chapter {:title "core.inject"
            :link "lucid.core.inject"
            :only ["in" "inject"]}]]

"`lucid.core.inject`' is used to create extra symbols in namespaces. It has been quite popular due this [article](http://dev.solita.fi/2014/03/18/pimp-my-repl.html)."

"Add to `project.clj` dependencies:

    [im.chit/lucid.core.inject \"{{PROJECT.version}}\"]

All functionality is in the `lucid.core.inject` namespace:"

(comment
  (use 'lucid.core.inject))

[[:api {:namespace "lucid.core.inject"
        :title ""
        :only ["in" "inject"]}]]

"`inject` enables both macros and functions to be imported:"

(comment
  (inject '[clojure.core [clojure.repl dir]])
  => [#'clojure.core/dir]

  (dir clojure.repl)
  ;; apropos
  ;; demunge
  ;; dir
  ;; dir-fn
  ;; doc
  ;; find-doc
  ;; pst
  ;; root-cause
  ;; set-break-handler!
  ;; source
  ;; source-fn
  ;; stack-element-str
  ;; thread-stopper
  )

"This function is extremely useful when adding additional functionality that is needed which is not included in `clojure.core`. It can be used to import both macros and funcions into a given namespace:"

"The macro `inject/in` enables better support:"

(comment
  ;; the default injected namespace is `.`
  (inject/in
   
   ;; note that `:refer, :all and :exclude can be used
   [lucid.core.inject :refer [inject [in inject-in]]]
   
   ;; imports all functions from lucid.package
   [lucid.package]
   
   ;; inject into clojure.core
   clojure.core
   [lucid.mind .> .? .* .% .%> .& .>ns .>var]
               
   ;; inject into `>` namespace
   >
   [clojure.pprint pprint]
   [clojure.java.shell sh])
  
  => [#'./inject
      #'./inject-in
      #'./pull
      #'./resolve-coordinates
      #'./jar-entry
      #'./add-url
      #'./resolve-jar
      #'./resolve-with-dependencies
      #'./maven-file
      #'./coordinate
      #'clojure.core/.>
      #'clojure.core/.?
      #'clojure.core/.*
      #'clojure.core/.%
      #'clojure.core/.%>
      #'clojure.core/.&
      #'clojure.core/.>ns
      #'clojure.core/.>var
      #'>/pprint
      #'>/sh])

[[:chapter {:title "core.java"
            :link "lucid.core.java"
            :only ["java-sources" "javac" "reimport"]}]]

"`lucid.core.java` is used to work with mixed java and clojure projects"

"Add to `project.clj` dependencies:

    [im.chit/lucid.core.java \"{{PROJECT.version}}\"]

All functionality is in the `lucid.core.java` namespace:"

(comment
  (use 'lucid.core.java))

[[:api {:namespace "lucid.core.java"
        :title ""
        :only ["java-sources" "javac" "reimport"]}]]

[[:chapter {:title "core.namespace" :link "lucid.core.namespace"}]]

"`lucid.core.namespace` provides additional namespace utilities."

"Add to `project.clj` dependencies:

    [im.chit/lucid.core.namespace \"{{PROJECT.version}}\"]

All functionality is in the `lucid.core.namespace` namespace:"

(comment
  (use 'lucid.core.namespace))

[[:api {:namespace "lucid.core.namespace"
        :title ""}]]
