(ns lucid.distribute.manifest.common-test
  (:use midje.sweet)
  (:require [lucid.distribute.manifest.common :refer :all]
            [lucid.distribute.analyser [java]]))

^{:refer lucid.distribute.manifest.common/build-filemap :added "1.2"}
(fact "builds manifest for resources and java folder"

  (build-filemap "example/repack.advance"
                  {:subpackage "resources"
                   :path "resources"
                   :distribute {"common" #{"common"}
                                "web"    #{"web"}}
                   :dependents #{"core"}})
  => (contains {"common"     anything  ;; {resources/common/b.txt resources/common/a.txt},
                "resources"  anything  ;; {resources/stuff/y.edn resources/stuff/x.edn},
                "web"        anything  ;; {resources/web/a.html resources/web/b.html}
                })

  (build-filemap "example/repack.advance"
                  {:subpackage "jvm"
                   :path "java/im/chit/repack"
                   :distribute {"common" #{"common"}
                                "web"    #{"web"}}
                   :dependents #{"core"}})
  => (contains {"common"     anything  ;; {java/im/chit/repack/common/Hello.java},
                "jvm"        anything  ;; {java/im/chit/repack/native/Utils.java},
                "web"        anything  ;; {java/im/chit/repack/web/Client.java}
                }))

(-> (build-filemap "example/repack.advance"
                   {:subpackage "jvm"
                    :path "java/im/chit/repack"
                    :distribute {"common" #{"common"}
                                 "web"    #{"web"}}
                    :dependents #{"core"}})
    (get "web")
    first
    (#(into {} %)))
