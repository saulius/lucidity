(ns documentation.lucid-distribute
  (:use hara.test))


[[:chapter {:title "Introduction"}]]

"`lucid.distribute` allows for splitting up a large project codebase into smaller distributions to enable better reuse."

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [im.chit/lucid.distribute "{{PROJECT.version}}"])

"All functionality is in the `lucid.distribute` namespace:"

(comment
  (use 'lucid.distribute))

[[:chapter {:title "API"}]]

[[:api {:title ""
        :namespace "lucid.distribute"}]]

