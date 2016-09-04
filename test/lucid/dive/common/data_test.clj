(ns lucid.dive.common.data-test
  (:use hara.test)
  (:require [lucid.dive.common.data :refer :all]))

^{:refer lucid.dive.common.data/folio :added "1.1"}
(fact "constructs a folio object")

^{:refer lucid.dive.common.data/references :added "1.1"}
(fact "constructs a reference object")
