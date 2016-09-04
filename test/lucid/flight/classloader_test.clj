(ns lucid.flight.classloader-test
  (:use hara.test)
  (:require [lucid.flight.classloader :refer :all]
            [hara.common.checks :as checks]
            [clojure.java.io :as io]))

^{:refer lucid.flight.classloader/to-bytes :added "1.1"}
(fact "opens `.class` file from an external source"
  (to-bytes "target/classes/test/Dog.class")
  => checks/bytes?)

^{:refer lucid.flight.classloader/path->classname :added "1.1"}
(fact "converts the path to a classname"
  (path->classname "test/Dog.class")
  => "test.Dog")

^{:refer lucid.flight.classloader/load-class :added "1.1"}
(fact "loads class from an external source")
