(ns lucid.wake.common.data-test
  (:use hara.test)
  (:require [lucid.wake.common.data :refer :all]))

^{:refer lucid.wake.common.data/folio :added "1.1"}
(fact "constructs a folio object")

^{:refer lucid.wake.common.data/references :added "1.1"}
(fact "constructs a reference object")
