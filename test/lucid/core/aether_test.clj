(ns lucid.core.aether-test
  (:use hara.test)
  (:require [lucid.core.aether :refer :all]))

^{:refer lucid.core.aether/aether :added "1.1"}
(fact "creates an `Aether` object"

  (into {} (aether))
  => +defaults+)

^{:refer lucid.core.aether/resolve-dependencies :added "1.1"}
(fact "resolves maven dependencies for a set of coordinates"

  (->> (resolve-dependencies '[im.chit/hara.test "2.4.4"])
       sort
       (take 5))
  => '[[im.chit/hara.class.inheritance "2.4.4"]
       [im.chit/hara.common "2.4.4"]
       [im.chit/hara.common.checks "2.4.4"]
       [im.chit/hara.common.error "2.4.4"]
       [im.chit/hara.common.hash "2.4.4"]])
