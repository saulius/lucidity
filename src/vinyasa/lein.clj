(ns vinyasa.lein
  (:require [clojure.java.io :as io]
            vinyasa.pull))

(defn init []
  (if-let [lein-version (get (System/getenv) "LEIN_VERSION")]
     (vinyasa.pull/pull 'leiningen lein-version)
     (throw (Exception. "Cannot find the variable LEIN_VERSION in System/getenv"))))

(require '[leiningen.core.main :as lein]
         '[leiningen.core.user :as user]
         '[leiningen.core.project :as project])

(defn lein-fn
  "Command-line entry point."
  [& raw-args]
  (try
    (user/init)
    (let [project (project/init-project
                   (if (.exists (io/file lein/*cwd* "project.clj"))
                     (project/read (str (io/file lein/*cwd* "project.clj")))
                     (-> (project/make {:eval-in :leiningen :prep-tasks []
                                        :source-paths ^:replace []
                                        :resource-paths ^:replace []
                                        :test-paths ^:replace []})
                         project/project-with-profiles
                         (project/init-profiles [:default]))))]
      (when (:min-lein-version project) (#'lein/verify-min-version project))
      (#'lein/configure-http)
      (#'lein/resolve-and-apply project raw-args))))

(defmacro lein [& args]
  `(lein-fn ~@(map str args)))