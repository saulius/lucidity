(ns documentation.lucid-mind
  (:use hara.test))

[[:chapter {:title "Introduction"}]]

"`lucid.mind` gives greater understanding of java objects" 

[[:chapter {:title "Installation"}]]

"Add to `project.clj` dependencies: 

`[tahto/lucid.mind `\"`{{PROJECT.version}}`\"`]`

All functionality is in the `lucid.mind` namespace:
"

(comment
  (use 'lucid.mind))
  
(fact
  (+ 1 1)
  => 2)

