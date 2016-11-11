(ns documentation.lucid-aether
  (:use hara.test))
  
  
[[:chapter {:title "core.aether"
            :link "lucid.aether"
            :only ["resolve-dependencies" "resolve-hierarchy"]}]]

  "`lucid.aether` is used to as an interface to manage dependencies. It is meant to replace [pomegranate](https://github.com/cemerick/pomegranate) for dependency resolution."

  "Add to `project.clj` dependencies:

      [tahto/lucid.aether \"{{PROJECT.version}}\"]

  All functionality is in the `lucid.aether` namespace:"

  (comment
    (use 'lucid.aether))

  [[:api {:namespace "lucid.aether"
          :only ["resolve-dependencies" "resolve-hierarchy"]
          :title ""}]]
  
  