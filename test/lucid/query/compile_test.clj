(ns lucid.query.compile-test
  (:use hara.test)
  (:require [lucid.query.compile :refer :all]
            [rewrite-clj.zip :as source]))

^{:refer lucid.query.compile/cursor-info :added "1.2"}
(fact "finds the information related to the cursor"

  (cursor-info '[(defn ^:?& _ | & _)])
  => '[0 :form (defn _ | & _)]

  (cursor-info (expand-all-metas '[(defn ^:?& _ | & _)]))
  => '[0 :form (defn _ | & _)]

  (cursor-info '[defn if])
  => [nil :cursor]

  (cursor-info '[defn | if])
  => [1 :cursor])

^{:refer lucid.query.compile/expand-all-metas :added "1.2"}
(fact "converts the shorthand meta into a map-based meta"
  (meta (expand-all-metas '^:%? sym?))
  => {:? true, :% true}

  (-> (expand-all-metas '(^:%+ + 1 2))
      first meta)
  => {:+ true, :% true})

^{:refer lucid.query.compile/split-path :added "1.2"}
(fact "splits the path into up and down"
  (split-path '[defn | if try] [1 :cursor])
  => '{:up (defn), :down [if try]}

  (split-path '[defn if try] [nil :cursor])
  => '{:up [], :down [defn if try]})

^{:refer lucid.query.compile/process-special :added "1.2"}
(fact "converts a keyword into a map"
  (process-special :*) => {:type :multi}

  (process-special :1) => {:type :nth, :step 1}

  (process-special :5) => {:type :nth, :step 5})

^{:refer lucid.query.compile/process-path :added "1.2"}
(fact "converts a path into more information"
  (process-path '[defn if try])
  => '[{:type :step, :element defn}
       {:type :step, :element if}
       {:type :step, :element try}]

  (process-path '[defn :* try :3 if])
  => '[{:type :step, :element defn}
       {:element try, :type :multi}
       {:element if, :type :nth, :step 3}])

^{:refer lucid.query.compile/compile-section-base :added "1.2"}
(fact "compiles an element section"
  (compile-section-base '{:element defn})
  => '{:form defn}

  (compile-section-base '{:element (if & _)})
  => '{:pattern (if & _)}

  (compile-section-base '{:element _})
  => {:is lucid.query.common/any})

^{:refer lucid.query.compile/compile-section :added "1.2"}
(fact "compile section"
  (compile-section :up nil '{:element if, :type :nth, :step 3})
  => '{:nth-ancestor [3 {:form if}]}

  (compile-section :down nil '{:element if, :type :multi})
  => '{:contains {:form if}})

^{:refer lucid.query.compile/compile-submap :added "1.2"}
(fact "compile submap"
  (compile-submap :down (process-path '[if try]))
  => '{:child {:child {:form if}, :form try}}

  (compile-submap :up (process-path '[defn if]))
  => '{:parent {:parent {:form defn}, :form if}})

^{:refer lucid.query.compile/prepare :added "1.2"}
(fact "prepare"
  (prepare '[defn if])
  => '[{:child {:form if}, :form defn} [nil :cursor]]
  
  (prepare '[defn | if])
  => '[{:parent {:form defn}, :form if} [1 :cursor]])

(comment
  (require '[rewrite-clj.zip :as z])
  
  (cursor-info '[(defn ^:?& _ | & _)])

  (cursor-info (expand-all-metas '[(defn ^:?& _ | & _)]))

  (potential-cursors (expand-all-metas '[(defn & _)]))

  ($ nil [(defn _ | & _)])

  (set! *print-meta* (not *print-meta*))
  )
