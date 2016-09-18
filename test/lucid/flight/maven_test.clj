(ns lucid.flight.maven-test
  (:use hara.test)
  (:require [lucid.flight.maven :refer :all]
            [lucid.flight.maven.jar-test :refer [*hara-test-path* *hara-version*]]))

^{:refer lucid.flight.maven/coordinate :added "1.1"}
(fact "creates a coordinate based on the path"

  (coordinate *hara-test-path*)
  => ['im.chit/hara.test *hara-version*])

^{:refer lucid.flight.maven/coordinate-dependencies :added "1.1"}
(fact "list dependencies for a coordinate"

  (coordinate-dependencies '[[im.chit/hara.test "2.4.4"]])
  => (contains '[[im.chit/hara.test "2.4.4"]
                 [im.chit/hara.namespace.import "2.4.4"]
                 [im.chit/hara.event "2.4.4"]
                 [im.chit/hara.common.primitives "2.4.4"]
                 [im.chit/hara.data.seq "2.4.4"]
                 [im.chit/hara.data.map "2.4.4"]
                 [im.chit/hara.common.checks "2.4.4"]
                 [im.chit/hara.common.primitives "2.4.4"]
                 [im.chit/hara.common.error "2.4.4"]
                 [im.chit/hara.common.checks "2.4.4"]
                 [im.chit/hara.io.file "2.4.4"]
                 [im.chit/hara.display.ansii "2.4.4"]]
               :in-any-order))

^{:refer lucid.flight.maven/resolve-jar :added "1.1"}
(fact "resolves a jar according to context"

  (resolve-jar 'hara.test)
  => [*hara-test-path* "hara/test.clj"])

^{:refer lucid.flight.maven/resolve-coordinates :added "1.1"}
(fact "resolves a set of coordinates"

  (resolve-coordinates 'hara.test)
  => ['im.chit/hara.test *hara-version*])


^{:refer lucid.flight.maven/resolve-with-deps :added "1.1"}
(fact "resolves the jar and path of a namespace"

  (resolve-with-deps 'hara.test)
  => [*hara-test-path* "hara/test.clj"]
  )

^{:refer lucid.flight.maven/pull :added "1.1"}
(fact "pulls down the necessary dependencies from maven and adds it to the project"

  (pull '[im.chit/hara.test "2.4.4"])
  => (contains '[[im.chit/hara.test "2.4.4"]
                 [im.chit/hara.namespace.import "2.4.4"]
                 [im.chit/hara.event "2.4.4"]
                 [im.chit/hara.common.primitives "2.4.4"]
                 [im.chit/hara.data.seq "2.4.4"]
                 [im.chit/hara.data.map "2.4.4"]
                 [im.chit/hara.common.checks "2.4.4"]
                 [im.chit/hara.common.primitives "2.4.4"]
                 [im.chit/hara.common.error "2.4.4"]
                 [im.chit/hara.common.checks "2.4.4"]
                 [im.chit/hara.io.file "2.4.4"]
                 [im.chit/hara.display.ansii "2.4.4"]]
               :in-any-order))
