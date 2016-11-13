(ns lucid.aether.base-test
  (:use hara.test)
  (:require [lucid.aether.base :refer :all]))

^{:refer lucid.aether.base/aether :added "1.1"}
(fact "creates an `Aether` object"

  (into {} (aether))
  => +defaults+)
