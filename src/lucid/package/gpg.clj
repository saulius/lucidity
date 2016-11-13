(ns lucid.package.gpg
  (:require [hara.io.file :as fs]
            [hara.security :as security])
  (:import java.security.Security
           org.bouncycastle.jce.provider.BouncyCastleProvider
           org.bouncycastle.openpgp.PGPSecretKeyRingCollection
           org.bouncycastle.openpgp.bc.BcPGPSecretKeyRingCollection))

(defonce +bouncy-castle+ 
  (Security/addProvider (BouncyCastleProvider.)))

(defn load-keyring [input]
  (-> (fs/input-stream input)
      (BcPGPSecretKeyRingCollection.)))

(defn save-keyring [keyring output]
  (->> (fs/output-stream output)
       (.encode keyring)))

(comment
  (def keyring (load-keyring "/Users/chris/.gnupg/secring.gpg"))
  (save-keyring keyring "hello.gpg")
  (load-keyring "hello.gpg"))
