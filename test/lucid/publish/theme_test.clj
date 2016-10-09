(ns lucid.publish.theme-test
  (:use hara.test)
  (:require [lucid.publish.theme :refer :all]
            [hara.io.project :as project]))

^{:refer lucid.publish.theme/load-var :added "1.2"}
(fact "loads a namespaced var"
  (load-var "clojure.core" "apply")
  => fn?)

^{:refer lucid.publish.theme/load-settings :added "1.2"}
(fact "load theme settings"
  (keys (load-settings "stark" (project/project)))
  => (contains [:email :date :copy :tracking-enabled
                :site :time :manifest :icon :defaults
                :theme :author :render :tracking :resource :engine]
               :in-any-order))

^{:refer lucid.publish.theme/apply-settings :added "1.2"}
(fact "applies function to the settings in the current `project.clj`")

^{:refer lucid.publish.theme/template-path :added "1.2"}
(fact "creates a template path, by default it is `./template`")

^{:refer lucid.publish.theme/refresh? :added "1.2"}
(fact "checks the `refresh` setting for the project template"

  (refresh?) ;; by default, it is false
  => false)

^{:refer lucid.publish.theme/deployed? :added "1.2"}
(comment "checks if a theme has been deployed"

  (deployed? (load-settings "stark" (project/project))
             (project/project))
  => true)

^{:refer lucid.publish.theme/deploy :added "1.2"}
(comment "deploys theme into template directory"

  ;; deploys the stark theme to the template/stark directory
  ;; overwriting any content in the directory
  (deploy (load-settings "stark" (project/project))
          (project/project)))
