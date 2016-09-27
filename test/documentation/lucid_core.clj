(ns documentation.lucid-core
  (:use hara.test)
  (:require [lucid.core
             [aether :refer :all]
             [classloader :refer :all]
             [debug :refer :all]
             [inject :refer :all]
             [namespace :refer :all]]))

[[:chapter {:title "Introduction"}]]

"`lucid.core` provides utilities that either support the rest of the `lucidity` suite or are useful standalone tools by themselves. Each one is installed individually and usually only provides one or two top level function for use:" 

[[:chapter {:title "aether"}]]

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

[[:chapter {:title "classloader"}]]

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [tahto/lucid.core.classloader "{{PROJECT.version}}"])
  
"All functionality is in the `lucid.core.classloader` namespace:"

(comment
  (use 'lucid.core.classloader))

[[:section {:title "load-class"}]]


[[:chapter {:title "debug"}]]

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [tahto/lucid.core.debug "{{PROJECT.version}}"])
  
"All functionality is in the `lucid.core.debug` namespace:"

(comment
  (use 'lucid.core.debug))

[[:chapter {:title "inject"}]]

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [tahto/lucid.core.inject "{{PROJECT.version}}"])
  
"All functionality is in the `lucid.core.inject` namespace:"

(comment
  (use 'lucid.core.inject))

[[:chapter {:title "namespace"}]]

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [tahto/lucid.core.namespace "{{PROJECT.version}}"])
  
"All functionality is in the `lucid.core.namespace` namespace:"

(comment
  (use 'lucid.core.namespace))
