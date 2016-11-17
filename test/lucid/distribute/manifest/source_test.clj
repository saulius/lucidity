(ns lucid.distribute.manifest.source-test
  (:use hara.test)
  (:require [clojure.java.io :as io]
            [hara.io.file :as fs]
            [lucid.distribute.manifest.source :refer :all]
            [lucid.distribute.manifest.common :as manifest]))

^{:refer lucid.distribute.manifest.source/child-dirs :added "1.2"}
(fact "lists all the child directories for a particular folder"
  (-> (io/file "example")
      (child-dirs)
      sort)
  => ["code.analysis"
      "distribute.advance"
      "distribute.simple"])

^{:refer lucid.distribute.manifest.source/split-path :added "1.2"}
(fact "splits the file into its path components"
  
  (split-path "repack/example/hello.clj")
  =>  ["repack" "example" "hello"])

^{:refer lucid.distribute.manifest.source/group-by-package :added "1.2"}
(fact "groups the source files by package"
  (->> ["common.clj"
        "core.clj"
        "util/array/sort.clj"
        "util/array.clj"
        "util/data.clj"
        "web/client.clj"
        "web.clj"]
       (mapv #(java.io.File.
               (str "example/distribute.advance/src/clj/repack/" %)))
       (group-by-package {:root (.toFile (fs/path "example/distribute.advance/src/clj/repack"))
                          :type :clojure
                          :levels 2
                          :path "src/clj"
                          :standalone #{"web"}}))
  => {"common" #{"common.clj"},
      "core" #{"core.clj"},
      "util.array" #{"util/array/sort.clj" "util/array.clj"},
      "util.data" #{"util/data.clj"},
      "web" #{"web.clj" "web/client.clj"}})

^{:refer lucid.distribute.manifest.common/build-filemap :added "1.2"}
(fact "builds manifest for clojure sources"

  (manifest/build-filemap "example/distribute.advance"
                          {:type :clojure
                           :levels 2
                           :path "src/clj"
                           :standalone #{"web"}})
  => (contains {"common"     anything ;; {src/clj/repack/common.clj}
                "core"       anything ;; {src/clj/repack/core.clj}
                "util.array" anything ;; {src/clj/repack/util/array.clj .. }
                "util.data"  anything ;; {src/clj/repack/util/data.clj}
                "web"        anything ;; {src/clj/repack/web.clj .. }
                })

  (manifest/build-filemap "example/distribute.advance"
                           {:type :clojure
                            :levels 2
                            :path "src/cljs"})
  => (contains {"web.client" anything ;; {src/cljs/repack/web/client.cljs},
                "web"        anything ;; {src/cljs/repack/web.cljs}
                })

  (manifest/build-filemap "example/distribute.advance"
                           {:type :clojure
                            :levels 2
                            :path "src/cljs"
                            :standalone #{"web"}})
  
  => (contains {"web"       anything ;; {src/cljs/repack/web.cljs .. }
                }))
