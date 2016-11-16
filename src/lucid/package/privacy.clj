(ns lucid.package.privacy
  (:require [hara.io.file :as fs]
            [hara.io.encode :as encode]
            [clojure.string :as string])
  (:import (java.security Security)
           (org.bouncycastle.jce.provider BouncyCastleProvider)
           (org.bouncycastle.openpgp.jcajce JcaPGPObjectFactory)
           (org.bouncycastle.openpgp.operator.jcajce JcePBESecretKeyDecryptorBuilder)
           (org.bouncycastle.openpgp.operator.bc BcKeyFingerprintCalculator
                                                 BcPublicKeyDataDecryptorFactory
                                                 BcPGPContentSignerBuilder)
           (org.bouncycastle.openpgp.bc BcPGPPublicKeyRingCollection
                                        BcPGPSecretKeyRingCollection)
           (org.bouncycastle.openpgp PGPObjectFactory
                                     PGPSignature
                                     PGPSignatureGenerator
                                     PGPUtil)))

(defonce +bouncy-castle+ 
  (Security/addProvider (BouncyCastleProvider.)))

(defn load-public-keyring [input]
  (-> (fs/input-stream input)
      (BcPGPPublicKeyRingCollection.)))

(defn load-secret-keyring [input]
  (-> (fs/input-stream input)
      (BcPGPSecretKeyRingCollection.)))

(defn save-keyring [keyring path]
  (->> (fs/output-stream path)
       (.encode keyring)))

(defn all-public-keys [rcoll]
  (->> (.getKeyRings rcoll)
       (iterator-seq)
       (map #(->> %
                  (.getPublicKeys)
                  (iterator-seq)))
       (apply concat)))

(defn fingerprint [pub]
  (-> pub
      (.getFingerprint)
      (hara.io.encode/to-hex)
      (.toUpperCase)))

(defn list-public-keys [rcoll]
  (->> (all-public-keys rcoll)
       (map fingerprint)))

(defn get-public-key [rcoll sig]
  (->> (all-public-keys rcoll)
       (filter #(-> %
                    (fingerprint)
                    (.contains (.toUpperCase sig))))
       (first)))

(defn all-secret-keys [rcoll]
  (->> (.getKeyRings rcoll)
       (iterator-seq)
       (map #(->> %
                  (.getSecretKeys)
                  (iterator-seq)))
       (apply concat)))

(defn get-secret-key
  [rcoll sig]
  (->> (all-secret-keys rcoll)
       (filter #(-> %
                    (.getPublicKey)
                    (fingerprint)
                    (.contains (.toUpperCase sig))))
       first))

(defn decrypt
  [encrypted-file keyring-file]
  (let [obj-factory  (-> (fs/input-stream encrypted-file)
                         (PGPUtil/getDecoderStream)
                         (PGPObjectFactory. (BcKeyFingerprintCalculator.)))
        ring         (-> (load-secret-keyring keyring-file)
                         (.getKeyRings)
                         (iterator-seq)
                         (first))
        enc-data     (-> (.nextObject obj-factory)
                         (.getEncryptedDataObjects)
                         (iterator-seq)
                         (first))
        key-id       (.getKeyID enc-data)
        prv-key      (-> (JcePBESecretKeyDecryptorBuilder.)
                         (.setProvider "BC")
                         (.build (char-array ""))
                         (->> (.extractPrivateKey (.getSecretKey ring key-id))))
        clear-stream (->> (BcPublicKeyDataDecryptorFactory. prv-key)
                          (.getDataStream enc-data)
                          (JcaPGPObjectFactory.)
                          (.nextObject)
                          (.getDataStream)
                          (JcaPGPObjectFactory.)
                          (.nextObject)
                          (.getDataStream))]
    (slurp clear-stream)))

(defn generate-signature [input keyring-file sig]
  (let [rcoll      (load-secret-keyring keyring-file)
        sec-key    (get-secret-key rcoll sig)
        prv-key    (-> (JcePBESecretKeyDecryptorBuilder.)
                       (.setProvider "BC")
                       (.build (char-array ""))
                       (->> (.extractPrivateKey sec-key)))
        signature  (-> (BcPGPContentSignerBuilder.
                        (..  sec-key getPublicKey getAlgorithm)
                        PGPUtil/SHA256)
                       (PGPSignatureGenerator.)
                       (doto
                           (.init PGPSignature/BINARY_DOCUMENT prv-key)
                         (.update (fs/read-all-bytes input)))
                       (.generate))]
    signature))

(defn sign
  [input output keyring-file sig]
  (let [signature  (generate-signature input keyring-file sig)]
    (->> (concat ["-----BEGIN PGP SIGNATURE-----"
                  "Version: GnuPG v2"
                  ""]
                 (->> (.getEncoded signature)
                      (encode/to-base64)
                      (partition-all 64)
                      (map #(apply str %)))
                 [""
                  "-----END PGP SIGNATURE-----"])
         (string/join "\n")
         (spit output))))

(comment

  (.& (generate-signature "project.clj"
                          lucid.package.user/GNUPG-SECRET
                          "98B9A74D"))
  
  
  )


