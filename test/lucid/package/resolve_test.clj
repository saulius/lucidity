(ns lucid.package.resolve-test
  (:use hara.test)
  (:require [lucid.package.resolve :refer :all]))

^{:refer lucid.package.resolve/list-dependencies :added "1.2"}
(comment "list the dependencies for a particular coordinate"
  
  (list-dependencies '[[im.chit/hara.test "2.4.8"]])
  => '[[im.chit/hara.class.enum "2.4.8"]
       [im.chit/hara.class.inheritance "2.4.8"]
       [im.chit/hara.common.checks "2.4.8"]
       [im.chit/hara.common.error "2.4.8"]
       [im.chit/hara.common.primitives "2.4.8"]
       [im.chit/hara.common.string "2.4.8"]
       [im.chit/hara.data.map "2.4.8"]
       [im.chit/hara.data.seq "2.4.8"]
       [im.chit/hara.event "2.4.8"]
       [im.chit/hara.io.ansii "2.4.8"]
       [im.chit/hara.io.file "2.4.8"]
       [im.chit/hara.io.project "2.4.8"]
       [im.chit/hara.namespace.import "2.4.8"]
       [im.chit/hara.protocol.string "2.4.8"]
       [im.chit/hara.string.case "2.4.8"]
       [im.chit/hara.test "2.4.8"]])

^{:refer lucid.package.resolve/resolve-with-dependencies :added "1.2"}
(comment "resolves an entry with all artifact dependencies"

  (resolve-with-dependencies 'hara.data.map
                             '[im.chit/hara.test "2.4.8"])
  => '[[im.chit/hara.data.map "2.4.8"]
       "hara/data/map.clj"])


^{:refer lucid.package.resolve/pull :added "1.1"}
(comment "pulls down the necessary dependencies from maven and adds it to the project"

  (pull '[org.clojure/core.match "0.2.2"])
  => '[[org.clojure/core.cache "0.6.3"]
       [org.clojure/core.match "0.2.2"]
       [org.clojure/core.memoize "0.5.6"]
       [org.clojure/data.priority-map "0.0.2"]
       [org.clojure/tools.analyzer "0.1.0-beta12"]
       [org.clojure/tools.analyzer.jvm "0.1.0-beta12"]
       [org.ow2.asm/asm-all "4.1"]])

(comment
  (./import))
