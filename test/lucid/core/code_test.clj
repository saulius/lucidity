(ns lucid.core.code-test
  (:use hara.test)
  (:require [lucid.core.code :refer :all]))

^{:refer lucid.core.code/analyse-file :added "1.2"}
(fact "analyses a source or test file for information"

  (analyse-file "src/lucid/core/code.clj")
  => (contains-in
      {'lucid.core.code
       {'analyse-file
        {:source {:code string?,
                  :line {:row number?
                         :col number?
                         :end-row number?
                         :end-col number?},
                  :path "src/lucid/core/code.clj"}}}})

  (analyse-file "test/lucid/core/code_test.clj")
  => (contains-in
      {'lucid.core.code
       {'analyse-file
        {:test {:code vector?
                :line {:row number?
                         :col number?
                         :end-row number?
                         :end-col number?}
                :path "test/lucid/core/code_test.clj"},
         :meta {:added "1.2"},
         :intro "analyses a source or test file for information"}}}))

^{:refer lucid.core.code/join-nodes :added "1.2"}
(fact "joins nodes together from a test"

  (-> (analyse-file "test/lucid/core/code_test.clj")
      (get-in ['lucid.core.code 'analyse-file :test :code])
      (join-nodes))
  => string?)
