(ns documentation.lucid-core
  (:use hara.test)
  (:require [lucid.core
             [aether :refer :all]
             [classloader :refer :all]
             [debug :refer :all]
             [inject :refer :all :as inject]
             [namespace :refer :all]]))

[[:chapter {:title "Introduction"}]]

"`lucid.core` provides utilities that either support the rest of the `lucidity` suite or are useful standalone tools by themselves. Each one is installed individually and usually only provides one or two top level function for use:" 

[[:chapter {:title "Aether"}]]

"This library is used to as an interface to manage dependencies. It is meant to replace [pomegranate](https://github.com/cemerick/pomegranate) for some tasks."

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [tahto/lucid.core.aether "{{PROJECT.version}}"])
  
"All functionality is in the `lucid.core.aether` namespace:"

(comment
  (use 'lucid.core.aether))

[[:section {:title "resolve"}]]

"`resolve-dependencies` is a very important function as it is used to synchronise many tasks so that libraries are downloaded as needed from maven:"

(fact
  (resolve-dependencies '[prismatic/schema "1.1.3"])
  => '[[prismatic/schema "1.1.3"]]
  
  (resolve-dependencies '[midje "1.6.3"])
  => '[[utilize/utilize "0.2.3"]
       [swiss-arrows/swiss-arrows "1.0.0"]
       [slingshot/slingshot "0.10.3"]
       [org.clojure/tools.namespace "0.2.4"]
       [org.clojure/tools.macro "0.1.5"]
       [org.clojure/math.combinatorics "0.0.7"]
       [org.clojure/core.unify "0.5.2"]
       [org.clojars.trptcolin/sjacket "0.1.3"]
       [ordered/ordered "1.2.0"]
       [net.cgrand/regex "1.1.0"]
       [net.cgrand/parsley "0.9.1"]
       [midje/midje "1.6.3"]
       [joda-time/joda-time "2.2"]
       [gui-diff/gui-diff "0.5.0"]
       [dynapath/dynapath "0.2.0"]
       [commons-codec/commons-codec "1.9"]
       [colorize/colorize "0.1.1"]
       [clj-time/clj-time "0.6.0"]])

[[:section {:title "hierarchy"}]]

"`resolve-hierarchy` shows the dependency hierachy instead of a flattened list:"

(fact
  (resolve-hierarchy '[midje "1.6.3"])
  => '{[midje/midje "1.6.3"]
       [{[ordered/ordered "1.2.0"] []}
        {[org.clojure/math.combinatorics "0.0.7"] []}
        {[org.clojure/core.unify "0.5.2"] []}
        {[utilize/utilize "0.2.3"]
         [{[org.clojure/tools.macro "0.1.1"] []}
          {[joda-time/joda-time "2.0"] []}
          {[ordered/ordered "1.0.0"] []}]}
        {[colorize/colorize "0.1.1"] []}
        {[org.clojure/tools.macro "0.1.5"] []}
        {[dynapath/dynapath "0.2.0"] []}
        {[swiss-arrows/swiss-arrows "1.0.0"] []}
        {[org.clojure/tools.namespace "0.2.4"] []}
        {[slingshot/slingshot "0.10.3"] []}
        {[commons-codec/commons-codec "1.9"] []}
        {[gui-diff/gui-diff "0.5.0"]
         [{[org.clojars.trptcolin/sjacket "0.1.3"]
           [{[net.cgrand/regex "1.1.0"] []}
            {[net.cgrand/parsley "0.9.1"]
             [{[net.cgrand/regex "1.1.0"] []}]}]}
          {[ordered/ordered "1.2.0"] []}]}
        {[clj-time/clj-time "0.6.0"]
         [{[joda-time/joda-time "2.2"] []}]}]})

[[:chapter {:title "Asm"}]]

"This library allows exploration of classes on the filesystem, independent of a classloader. Warning, it may be very frustrating to use, however, the tool gives the user a lot of control."

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [tahto/lucid.core.asm "{{PROJECT.version}}"])
  
"All functionality is in the `lucid.core.asm` namespace:"

(comment
  (use 'lucid.core.asm))

[[:section {:title "load-class"}]]

"Enables the loading of class files in the directory"

(comment
  (load-class "target/classes/test/Cat.class")
  => test.Cat)

"Can load class files within a jar:"

(comment
  (load-class "<.m2>/org/yaml/snakeyaml/1.5/snakeyaml-1.5.jar"
              "org/yaml/snakeyaml/Dumper.class")
  => org.yaml.snakeyaml.Dumper)

"As well as with a coordinate:"

(comment
  (load-class '[org.yaml/snakeyaml "1.5"]
              "org/yaml/snakeyaml/Dumper.class")
  => org.yaml.snakeyaml.Dumper)

[[:chapter {:title "Code"}]]

"Source and test code analysis"

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [tahto/lucid.core.code "{{PROJECT.version}}"])
  
"All functionality is in the `lucid.core.code` namespace:"

(comment
  (use 'lucid.core.code))
  



[[:chapter {:title "Debug"}]]

"Macros and helpers for debugging"

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [tahto/lucid.core.debug "{{PROJECT.version}}"])
  
"All functionality is in the `lucid.core.debug` namespace:"

(comment
  (use 'lucid.core.debug))

[[:section {:title "dbg->"}]]

"The function acts the same way as the `->` thrush macro but also prints out each step of the output:"

(comment
  (dbg-> 1
         inc
         (+ 10 1 1))
  => 14
  ;; 1
  ;; -> inc :: 2
  ;; ->  (+ 10 1 1) :: 14
  )

"It works with all data:"

(comment
  (dbg-> {:a 1}
         (update-in [:a] inc)
         (merge {:c 3 :d 4})
         (select-keys [:a :d]))
  => {:a 2, :d 4}
  ;; {:a 1}
  ;; -> (update-in [:a] inc) :: {:a 2}
  ;; -> (merge {:c 3, :d 4}) :: {:a 2, :c 3, :d 4}
  ;; -> (select-keys [:a :d]) :: {:a 2, :d 4}
)

[[:section {:title "dbg->>"}]]

"The function acts the same way as the `->>` thrush last macro but also prints out each step of the output:"

(comment
  (dbg->> [1 2 3 4 5]
          (map inc)
          (filter even?)
          (concat ["a" "b" "c"]))
  => ("a" "b" "c" 2 4 6)
  ;; [1 2 3 4 5]
  ;; ->> (map inc) :: (2 3 4 5 6)
  ;; ->> (filter even?) :: (2 4 6)
  ;; ->> (concat [a b c]) :: (a b c 2 4 6)
)

[[:chapter {:title "Inject"}]]

"`inject`' has been quite popular due this [article](http://dev.solita.fi/2014/03/18/pimp-my-repl.html). It's main use is to create extra symbols in a particular namespace, namely `clojure.core`."

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [tahto/lucid.core.inject "{{PROJECT.version}}"])
  
"All functionality is in the `lucid.core.inject` namespace:"

(comment
  (use 'lucid.core.inject))

[[:section {:title "inject"}]]

"This function is extremely useful when adding additional functionality that is needed which is not included in `clojure.core`. It can be used to import both macros and funcions into a given namespace:"

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

[[:section {:title "in"}]]

"A helper macro `inject/in` enables better support:"

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

[[:chapter {:title "Namespace"}]]

"Better development experience by providing some additional namespace utilities."

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [tahto/lucid.core.namespace "{{PROJECT.version}}"])
  
"All functionality is in the `lucid.core.namespace` namespace:"

(comment
  (use 'lucid.core.namespace))

[[:section {:title "clear-aliases"}]]

"Manually gets rid of all namespace aliases in the current namespace"

(comment

  ;; require clojure.string
  (require '[clojure.string :as string])
  => nil

  ;; error if a new namespace is set to the same alias
  (require '[clojure.set :as string])
  => (throws "Alias string already exists in namespace")

  ;; clearing all aliases
  (clear-aliases)
  (ns-aliases *ns*)
  => {}

  ;; okay to require
  (require '[clojure.set :as string])
  => nil)

[[:section {:title "clear-mappings"}]]

"Manually gets rid of all interned symbols in the current namespace"

(comment

  ;; require `join`
  (require '[clojure.string :refer [join]])

  ;; check that it runs
  (join ["a" "b" "c"])
  => "abc"

  ;; clear mappings
  (clear-mappings)
  
  ;; the mapped symbol is gone
  (join ["a" "b" "c"])
  => (throws "Unable to resolve symbol: join in this context"))
