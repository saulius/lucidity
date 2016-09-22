(ns lucid.core.maven.file-test
  (:use hara.test)
  (:require [lucid.core.maven.file :refer :all]))

^{:refer lucid.core.maven.file/resource-symbol-path :added "1.1"}
(fact "creates a path based on symbol"
  (resource-symbol-path 'hara.test)
  => "hara/test.clj"

  (resource-symbol-path 'version-clj.core)
  => "version_clj/core.clj")

^{:refer lucid.core.maven.file/resource-path :added "1.1"}
(fact "creates a path based item"
  (resource-path "hello/world.txt")
  => "hello/world.txt"

  (resource-path 'version-clj.core)
  => "version_clj/core.clj"

  (resource-path java.io.File)
  => "java/io/File.class")
