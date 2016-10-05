(defproject im.chit/lucid "1.2.0"
  :description "tools for code clarity"
  :url "https://www.github.com/zcaudate/lucidity"
  :license {:name "The MIT License"
            :url "http://http://opensource.org/licenses/MIT"}
  :aliases {"test" ["run" "-m" "hara.test" ":exit"]}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.match     "0.2.2"]
                 [im.chit/hara.common.checks "2.4.5"]
                 [im.chit/hara.data.diff     "2.4.5"]
                 [im.chit/hara.data.nested   "2.4.5"]
                 [im.chit/hara.event         "2.4.5"]
                 [im.chit/hara.io.file       "2.4.5"]
                 [im.chit/hara.io.project    "2.4.5"]
                 [im.chit/hara.namespace     "2.4.5"]
                 [im.chit/hara.reflect       "2.4.5"]
                 [im.chit/hara.string        "2.4.5"]
                 [im.chit/hara.test          "2.4.5"]
                 
                 [org.eclipse.aether/aether-api "1.1.0"]
                 [org.eclipse.aether/aether-spi "1.1.0"]
                 [org.eclipse.aether/aether-util "1.1.0"]
                 [org.eclipse.aether/aether-impl "1.1.0"]
                 [org.eclipse.aether/aether-connector-basic "1.1.0"]
                 [org.eclipse.aether/aether-transport-wagon "1.1.0"]
                 [org.eclipse.aether/aether-transport-http "1.1.0"]
                 [org.eclipse.aether/aether-transport-file "1.1.0"]
                 [org.eclipse.aether/aether-transport-classpath "1.1.0"]
                 [org.apache.maven/maven-aether-provider "3.1.0"]
                 
                 [version-clj/version-clj "0.1.2"]
                 [rewrite-clj/rewrite-clj "0.5.2"]
                 [markdown-clj/markdown-clj "0.9.89"]
                 [hiccup/hiccup "1.0.5"]
                 [stencil/stencil "0.5.0"]]
                 
  :publish {:theme  "stark"
            
            :template {:site   "lucid"
                       :author "Chris Zheng"
                       :email  "z@caudate.me"
                       :icon   "favicon"
                       :tracking-enabled "true"
                       :tracking "UA-31320512-2"}
            
            :files {"index"
                    {:template "home.html"
                     :input "test/documentation/home_lucidity.clj"
                     :title "lucidity"
                     :subtitle "tools for code clarity"}
                    "lucid-core"
                    {:input "test/documentation/lucid_core.clj"
                     :title "core"
                     :subtitle "functions for the code environment"}
                    "lucid-mind"
                    {:input "test/documentation/lucid_mind.clj"
                     :title "mind"
                     :subtitle "contemplative reflection for the jvm"}
                    "lucid-publish"
                    {:input "test/documentation/lucid_publish.clj"
                     :title "publish"
                     :subtitle "generate documentation from code"}
                    "lucid-query"
                    {:input "test/documentation/lucid_query.clj"
                     :title "query"
                     :subtitle "intuitive search for code"}
                    "lucid-space"
                    {:input "test/documentation/lucid_space.clj"
                     :title "space"
                     :subtitle "management of project externals"}
                    "lucid-unit"
                    {:input "test/documentation/lucid_unit.clj"
                     :title "unit"
                     :subtitle "metadata through unit tests"}}
   
            :link {:auto-tag    true
                   :auto-number  true}}
  :profiles {:dev {:dependencies [[compojure "1.4.0"]
                                  [ring "1.4.0"]
                                  [clj-http "2.1.0"]
                                  [org.eclipse.jgit "4.0.1.201506240215-r"]]
                   :plugins [[lein-repack "0.2.10"]]}}
  :repack [{:type :clojure
            :levels 2
            :path "src"
            :standalone #{"deploy" "flow" "mind" "publish" "query" "space" "test"}}
           {:subpackage "resources"
            :path "resources"
            :distribute {"publish" #{"publish"}}}]

  :java-source-paths ["example/java"]
  :jar-exclusions [#"^test\..+\.class"])
