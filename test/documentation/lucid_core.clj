(ns documentation.lucid-core
  (:use hara.test)
  (:require [lucid.core
             [aether :refer :all]
             [asm :refer :all]
             [debug :refer :all]
             [inject :refer :all :as inject]
             ;;[namespace :refer :all]
             ]))

"`lucid.core` provides utilities that either support the rest of the `lucidity` suite or are useful standalone tools by themselves. Each one is installed individually and usually only provides one or two top level function for use:" 

[[:chapter {:title "core.aether"
            :link "lucid.core.aether"
            :only ["resolve-dependencies" "resolve-hierarchy"]}]]

"`lucid.core.aether` is used to as an interface to manage dependencies. It is meant to replace [pomegranate](https://github.com/cemerick/pomegranate) for dependency resolution."

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [tahto/lucid.core.aether "{{PROJECT.version}}"])
  
"All functionality is in the `lucid.core.aether` namespace:"

(comment
  (use 'lucid.core.aether))

[[:api {:namespace "lucid.core.aether"
        :only ["resolve-dependencies" "resolve-hierarchy"]
        :title ""}]]


[[:chapter {:title "core.asm"
            :link "lucid.core.asm"}]]

"`lucid.core.asm` allows exploration of classes on the filesystem, independent of the JVM classloader."

"Add to `project.clj`:"

[[{:stencil true}]]
(comment
  [tahto/lucid.core.asm "{{PROJECT.version}}"])
  
"All functionality is in the `lucid.core.asm` namespace:"

(comment
  (use 'lucid.core.asm))

"The library enables the loading of class files in the directory:"

(comment
  (load-class "target/classes/test/Cat.class")
  => test.Cat)

"Within a jar:"

(comment
  (load-class "<.m2>/org/yaml/snakeyaml/1.5/snakeyaml-1.5.jar"
              "org/yaml/snakeyaml/Dumper.class")
  => org.yaml.snakeyaml.Dumper)

"Within a coordinate:"

(comment
  (load-class '[org.yaml/snakeyaml "1.5"]
              "org/yaml/snakeyaml/Dumper.class")
  => org.yaml.snakeyaml.Dumper)


[[:api {:namespace "lucid.core.asm"
        :title ""}]]


[[:chapter {:title "core.code"
            :link "lucid.core.code"
            :only ["analyse-file"]}]]

"`lucid.core.code` is analyses source and test code and is used by `lucid.publish` and `lucid.unit` to build additional functionality."

"Add to `project.clj`:"

[[{:stencil true}]]
(comment
  [tahto/lucid.core.code "{{PROJECT.version}}"])
  
"All functionality is in the `lucid.core.code` namespace:"

(comment
  (use 'lucid.core.code))

[[:api {:namespace "lucid.core.code"
        :only ["analyse-file"]
        :title ""}]]

[[:chapter {:title "core.debug"
            :link "lucid.core.debug"
            :only ["dbg->" "dbg->>" "->doto" "->>doto" "->prn"]}]]

"`lucid.core.debug` contains macros and helpers for debugging"

"Add to `project.clj`:"

[[{:stencil true}]]
(comment
  [tahto/lucid.core.debug "{{PROJECT.version}}"])
  
"All functionality is in the `lucid.core.debug` namespace:"

(comment
  (use 'lucid.core.debug))

[[:api {:namespace "lucid.core.debug"
        :title ""
        :only ["dbg->" "dbg->>" "->doto" "->>doto" "->prn"]}]]

[[:chapter {:title "core.inject"
            :link "lucid.core.inject"
            :only ["in" "inject"]}]]

"`lucid.core.inject`' is used to create extra symbols in namespaces. It has been quite popular due this [article](http://dev.solita.fi/2014/03/18/pimp-my-repl.html)."

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [tahto/lucid.core.inject "{{PROJECT.version}}"])
  
"All functionality is in the `lucid.core.inject` namespace:"

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
   
   ;; imports all functions from lucid.space
   [lucid.space]
   
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

[[:chapter {:title "core.namespace" :link "lucid.core.namespace"}]]

"`lucid.core.namespace` provides additional namespace utilities."

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [tahto/lucid.core.namespace "{{PROJECT.version}}"])
  
"All functionality is in the `lucid.core.namespace` namespace:"

(comment
  (use 'lucid.core.namespace))

[[:api {:namespace "lucid.core.namespace"
        :title ""}]]
