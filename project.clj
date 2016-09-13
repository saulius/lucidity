(defproject tahto/lucid "1.1.0"
  :description "tools for clarity"
  :url "https://www.github.com/tahto/lucid"
  :license {:name "The MIT License"
            :url "http://http://opensource.org/licenses/MIT"}
  :aliases {"test" ["run" "-m" "hara.test"]}
  :injections [(require '[lucid.flight.inject :as inject])
	           (inject/in [lucid.flight.inject :refer [inject [in inject-in]]]
	                      [clojure.pprint pprint]
	                      [clojure.java.shell sh]
	                      [clojure.repl doc source]

	                      clojure.core
	                      [lucid.flight.reflection .& .> .? .* .% .%>]

	                      clojure.core
	                      [lucid.flight.debug :refer [[dbg-> *->] [dbg->> *->>]]])]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.match "0.2.2"]
                 [im.chit/hara.data      "2.4.2"]
                 [im.chit/hara.data.diff "2.4.2"]
                 [im.chit/hara.io.file "2.4.2"]
                 [im.chit/hara.io.watch  "2.4.2"]
                 [im.chit/hara.common.checks "2.4.2"]
                 [im.chit/hara.common.watch  "2.4.2"]
                 [im.chit/hara.component "2.4.2"]
                 [im.chit/hara.concurrent.latch "2.4.2"]
                 [im.chit/hara.concurrent.pipe  "2.4.2"]
                 [im.chit/hara.event     "2.4.2"]
                 [im.chit/hara.object    "2.4.2"]
                 [im.chit/hara.reflect   "2.4.2"]
                 [im.chit/hara.string    "2.4.2"]
				 [im.chit/jai "0.2.12"]
                 [tahto/wu.kong "0.1.4"]
                 [version-clj/version-clj "0.1.2"]
                 [rewrite-clj/rewrite-clj "0.5.1"]
                 [markdown-clj/markdown-clj "0.9.89"]
                 [hiccup/hiccup "1.0.5"]]
  :java-source-paths ["example/java"]
  :jar-exclusions [#"^test\..+\.class"]				 
  :profiles {:dev {:dependencies [[im.chit/hara.test "2.4.2"]]
                   :plugins [[lein-hydrox "0.1.17"]
				             [lein-repack "0.2.10"]]}}

  :repack [{:type :clojure
            :levels 2
            :path "src"
            :standalone #{"dive" "launch"}}])
