(ns lucid.distribute.manifest.source-test
  (:use midje.sweet)
  (:require [clojure.java.io :as io]
            [lucid.distribute.manifest.source :refer :all]
            [lucid.distribute.manifest.common :as manifest]))

^{:refer lucid.distribute.manifest.source/child-dirs :added "1.2"}
(fact "lists all the child directories for a particular folder"
  (child-dirs (io/file "example"))
  => (just ["repack.advance" "repack.simple"] :in-any-order))

^{:refer lucid.distribute.manifest.source/split-path :added "1.2"}
(fact "splits the file into its path components"
  (split-path "repack/example/hello.clj")
  =>  ["repack" "example" "hello"])

^{:refer lucid.distribute.manifest.common/build-filemap :added "1.2"}
(fact "builds manifest for clojure sources"

  (manifest/build-filemap "example/repack.advance"
                           {:type :clojure
                            :levels 2
                            :path "src/clj"
                            :package #{"web"}})
  => (contains {"common"     anything ;; {src/clj/repack/common.clj}
                "core"       anything ;; {src/clj/repack/core.clj}
                "util.array" anything ;; {src/clj/repack/util/array.clj .. }
                "util.data"  anything ;; {src/clj/repack/util/data.clj}
                "web"        anything ;; {src/clj/repack/web.clj .. }
                })

  (manifest/build-filemap "example/repack.advance"
                           {:type :clojure
                            :levels 2
                            :path "src/cljs"})
  => (contains {"web.client" anything ;; {src/cljs/repack/web/client.cljs},
                "web"        anything ;; {src/cljs/repack/web.cljs}
                })

  (manifest/build-filemap "example/repack.advance"
                           {:type :clojure
                            :levels 2
                            :path "src/cljs"
                            :package #{"web"}})
  => (contains {"web"       anything ;; {src/cljs/repack/web.cljs .. }
                }))
