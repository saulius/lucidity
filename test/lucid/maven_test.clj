(ns lucid.library-test
  (:use hara.test)
  (:require [lucid.library :refer :all]
            [lucid.library.jar-test :refer [*match-path* *match-version*]]))

^{:refer lucid.library/coordinate :added "1.1"}
(fact "creates a coordinate based on the path"

  (coordinate *match-path*)
  => ['org.clojure/core.match *match-version*])

^{:refer lucid.library/coordinate-dependencies :added "1.1"}
(fact "list dependencies for a coordinate"

  (coordinate-dependencies [['org.clojure/core.match *match-version*]])
  => [])

^{:refer lucid.library/resolve-jar :added "1.1"}
(fact "resolves a jar according to context"

  (resolve-jar 'clojure.core.match)
  => [*match-path* "clojure/core/match.clj"])

^{:refer lucid.library/resolve-coordinates :added "1.1"}
(fact "resolves a set of coordinates"

  (resolve-coordinates 'clojure.core.match)
  => ['org.clojure/core.match *match-version*])


^{:refer lucid.library/resolve-with-deps :added "1.1"}
(fact "resolves the jar and path of a namespace"

  (resolve-with-deps 'clojure.core.match)
  ;;=> [*match-path* "clojure/core/match.clj"]
  )

^{:refer lucid.library/pull :added "1.1"}
(fact "pulls down the necessary dependencies from maven and adds it to the project"

  (pull ['org.clojure/core.match *match-version*])
  => nil)

