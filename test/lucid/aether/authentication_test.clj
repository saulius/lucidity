(ns lucid.aether.authentication-test
  (:use hara.test)
  (:require [lucid.aether.authentication :refer :all]
            [hara.object :as object])
  (:import (org.eclipse.aether.repository Authentication)
           (org.eclipse.aether.util.repository AuthenticationBuilder
                                               ChainedAuthentication
                                               StringAuthentication
                                               SecretAuthentication)))

^{:refer lucid.aether.authentication/string-authentication :added "1.2"}
(fact "creates a `StringAuthentication` from a vector"

  (object/from-data [:username "chris"]
                    StringAuthentication)
  ;;=> #auth.string[:username "chris"]
  )

^{:refer lucid.aether.authentication/secret-authentication :added "1.2"}
(fact "creates a `SecretAuthentication` from a vector"

  (object/from-data [:password "hope"]
                    SecretAuthentication)
  ;;=> #auth.secret[:password "hope"]
  )

^{:refer lucid.aether.authentication/auth-map :added "1.2"}
(fact "creates a map of the `:authentications` element"

  (auth-map (-> (AuthenticationBuilder.)
                (.addUsername "chris")
                (.addPassword "hope")))
  => {:username "chris" :password "hope"})

^{:refer lucid.aether.authentication/chained-authentication :added "1.2"}
(comment "creates a `ChainedAuthentication` from a map"

  (object/from-data {:username "chris" :password "hope"}
                    ChainedAuthentication)
  ;;=> #auth.chained{:username "chris", :password "hope"
  )

^{:refer lucid.aether.authentication/authentication-builder :added "1.2"}
(comment "creates a `ChainedAuthentication` from a map"

  (object/from-data {:username "chris" :password "hope"}
                    AuthenticationBuilder)
  ;;=> #builder.auth{:username "chris", :password "hope"}
  )
