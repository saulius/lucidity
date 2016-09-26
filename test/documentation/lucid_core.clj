(ns documentation.lucid-core
  (:use hara.test))

[[:chapter {:title "Introduction"}]]

"**lucid.core** gives greater understanding of java objects" 

[[:chapter {:title "Installation"}]]

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [tahto/lucid.mind "{{PROJECT.version}}"])

"All functionality is in the `lucid.mind` namespace:"

[[:chapter {:title "core.aether"}]]
[[:chapter {:title "core.classloader"}]]
[[:chapter {:title "core.debug"}]]
[[:chapter {:title "core.inject"}]]
[[:chapter {:title "core.namespace"}]]

[[:chapter {:title "Installation"}]]

(comment
  (use 'lucid.mind))
  
(fact
  (+ 1 1)
  => 2)

