(ns lucid.security.opengpg
  (:require [hara.io.file :as fs])
  (:import java.security.Security
           org.bouncycastle.jce.provider.BouncyCastleProvider
           org.bouncycastle.openpgp.PGPSecretKeyRingCollection
           org.bouncycastle.openpgp.bc.BcPGPSecretKeyRingCollection))

(Security/addProvider
 (BouncyCastleProvider.))

(comment

  (def provs (seq (Security/getProviders)))
  (map type provs)
  (sun.security.provider.Sun
   sun.security.rsa.SunRsaSign
   sun.security.ec.SunEC
   com.sun.net.ssl.internal.ssl.Provider
   com.sun.crypto.provider.SunJCE
   sun.security.jgss.SunProvider
   com.sun.security.sasl.Provider
   org.jcp.xml.dsig.internal.dom.XMLDSigRI
   sun.security.smartcardio.SunPCSC
   apple.security.AppleProvider
   org.bouncycastle.jce.provider.BouncyCastleProvider)

  (->> (for [prov (seq (Security/getProviders))
             serv (seq (.getServices prov))]
         serv)
       (filter #(= "Cipher" (.getType %)))
       (map #(.getAlgorithm %))
       (sort))
  ("AES" "AESWrap" "AESWrap_128" "AESWrap_192" "AESWrap_256" "AES_128/CBC/NoPadding" "AES_128/CFB/NoPadding" "AES_128/ECB/NoPadding" "AES_128/GCM/NoPadding" "AES_128/OFB/NoPadding" "AES_192/CBC/NoPadding" "AES_192/CFB/NoPadding" "AES_192/ECB/NoPadding" "AES_192/GCM/NoPadding" "AES_192/OFB/NoPadding" "AES_256/CBC/NoPadding" "AES_256/CFB/NoPadding" "AES_256/ECB/NoPadding" "AES_256/GCM/NoPadding" "AES_256/OFB/NoPadding" "ARCFOUR" "Blowfish" "DES" "DESede" "DESedeWrap" "PBEWithHmacSHA1AndAES_128" "PBEWithHmacSHA1AndAES_256" "PBEWithHmacSHA224AndAES_128" "PBEWithHmacSHA224AndAES_256" "PBEWithHmacSHA256AndAES_128" "PBEWithHmacSHA256AndAES_256" "PBEWithHmacSHA384AndAES_128" "PBEWithHmacSHA384AndAES_256" "PBEWithHmacSHA512AndAES_128" "PBEWithHmacSHA512AndAES_256" "PBEWithMD5AndDES" "PBEWithMD5AndTripleDES" "PBEWithSHA1AndDESede" "PBEWithSHA1AndRC2_128" "PBEWithSHA1AndRC2_40" "PBEWithSHA1AndRC4_128" "PBEWithSHA1AndRC4_40" "RC2" "RSA")

  
  

  (->> (for [prov (seq (Security/getProviders))
             serv (seq (.getServices prov))]
         serv)
       (filter #(= "KeyGenerator" (.getType %)))
       (map #(.getAlgorithm %))
       (sort))
  ("AES" "ARCFOUR" "Blowfish" "DES" "DESede" "HmacMD5" "HmacSHA1" "HmacSHA224" "HmacSHA256" "HmacSHA384" "HmacSHA512" "RC2" "SunTls12Prf" "SunTlsKeyMaterial" "SunTlsMasterSecret" "SunTlsPrf" "SunTlsRsaPremasterSecret")

  (->> (for [prov (seq (Security/getProviders))
             serv (seq (.getServices prov))]
         serv)
       (filter #(= "KeyPairGenerator" (.getType %)))
       (map #(.getAlgorithm %))
       (sort))
  => ("DSA" "DiffieHellman" "EC" "RSA" "RSA")

  (->> (for [prov (seq (Security/getProviders))
             serv (seq (.getServices prov))]
       (.getType serv))
     (set)
     (sort))
  ("AlgorithmParameterGenerator" "AlgorithmParameters"
   "CertPathBuilder" "CertPathValidator" "CertStore" "CertificateFactory"
   "Cipher" "Configuration" "GssApiMechanism" "KeyAgreement"
   "KeyFactory" "KeyGenerator" "KeyInfoFactory" "KeyManagerFactory" "KeyPairGenerator" "KeyStore"
   "Mac" "MessageDigest" "Policy" "SSLContext" "SaslClientFactory" "SaslServerFactory" "SecretKeyFactory" "SecureRandom" "Signature" "TerminalFactory" "TransformService" "TrustManagerFactory" "XMLSignatureFactory")

  (->> (for [prov (seq (Security/getProviders))
             serv (seq (.getServices prov))]
       (.getType serv))
     (set)
     (sort))

  (->> (for [prov (seq (Security/getProviders))
             serv (seq (.getServices prov))]
         serv)
       (filter #(= "SecureRandom" (.getType %)))
       (map #(.getAlgorithm %))
       (sort))
  => ("NativePRNG" "NativePRNGBlocking" "NativePRNGNonBlocking" "SHA1PRNG")

  (->> (for [prov (seq (Security/getProviders))
             serv (seq (.getServices prov))]
         serv)
       (filter #(= "MessageDigest" (.getType %)))
       (map #(.getAlgorithm %))
       (sort))
  => ("MD2" "MD5" "SHA" "SHA-224" "SHA-256" "SHA-384" "SHA-512")
  
  (->> (for [prov (seq (Security/getProviders))
             serv (seq (.getServices prov))]
         serv)
       (filter #(= "Signature" (.getType %)))
       (map #(.getAlgorithm %))
       (sort))
  => ("MD2withRSA" "MD2withRSA" "MD5andSHA1withRSA" "MD5withRSA" "MD5withRSA" "NONEwithDSA" "NONEwithECDSA" "SHA1withDSA" "SHA1withECDSA" "SHA1withRSA" "SHA1withRSA" "SHA224withDSA" "SHA224withECDSA" "SHA224withRSA" "SHA256withDSA" "SHA256withECDSA" "SHA256withRSA" "SHA384withECDSA" "SHA384withRSA" "SHA512withECDSA" "SHA512withRSA")

  (->> (for [prov (seq (Security/getProviders))
             serv (seq (.getServices prov))]
         serv)
       (filter #(= "Configuration" (.getType %)))
       (map #(.getAlgorithm %))
       (sort))
  => ("JavaLoginConfig")

  (->> (for [prov (seq (Security/getProviders))
             serv (seq (.getServices prov))]
         serv)
       (filter #(= "KeyStore" (.getType %)))
       (map #(.getAlgorithm %))
       (sort))
  ("CaseExactJKS" "DKS" "JCEKS" "JKS" "KeychainStore" "PKCS12")
  
  (.? (nth provs 4) :name)

  
  
  (Security/removeProvider "BouncyCastleProvider")
  (type (first provs))
  )


(comment
  (def gen (javax.crypto.KeyGenerator/getInstance "AES/" "BC"))
  (def gen (javax.crypto.KeyGenerator/getInstance "AES/CBC/PKCS5PADDING"))
  (def gen (javax.crypto.KeyGenerator/getInstance "AES/CBC/PKCS5Padding" "BC"))
  
  (def gen (javax.crypto.KeyGenerator/getInstance "Camellia" "BC"))
  (def gen (javax.crypto.KeyGenerator/getInstance "CamelliA" "BC"))
  (def gen (javax.crypto.KeyGenerator/getInstance "CAMELLIA" "BC"))
  (def gen (javax.crypto.KeyGenerator/getInstance "1.2.392.200011.61.1.1.3.3" "BC"))
  (def gen (javax.crypto.KeyGenerator/getInstance "CAMELLIA-GMAC" "BC"))
  ""
  (def gen (javax.crypto.KeyGenerator/getInstance "Twofish" "BC"))
  (def gen (javax.crypto.KeyGenerator/getInstance "TWOFISH"))
  
  (.? gen :name)
  (def gen-p (.& gen))

  (keys gen-p)
  (:serviceIterator :initKeySize :initRandom :initParams :algorithm :lock :initType :spi :provider)
  
  
  (dissoc (into {} gen-p) :provider)
  
  
  (filter #(= (val %) "org.bouncycastle.jcajce.provider.symmetric.Camellia$KeyGen")
          (:provider gen-p))
  
  
  
  
  (spit "private.txt"
        (reduce-kv (fn [out k v]
                     (if (.startsWith k "KeyGenerator")
                       (assoc out (subs k (count "KeyGenerator.")) (Class/forName v))
                       out))
                   {}
                   (into {} (:provider gen-p))))
  
  (spit "public.txt"
        (reduce-kv (fn [out k v]
                     (if (.startsWith k "KeyPairGenerator")
                       (assoc out (subs k (count "KeyPairGenerator.")) (Class/forName v))
                       out))
                   {}
                   (into {} (:provider gen-p))))

  (spit "hello.txt" (into {} (:provider gen-p)))
  
  
  
  (.? gen)
  
  )

(defn load-keyring [input]
  (-> (fs/input-stream input)
      (BcPGPSecretKeyRingCollection.)))

(defn save-keyring [keyring output]
  (->> (fs/output-stream output)
       (.encode keyring)))

(comment
  (def keyring (load-keyring "/Users/chris/.gnupg/secring.gpg"))
  (save-keyring keyring "hello.gpg")
  (load-keyring "hello.gpg")
  
  (def secret (-> "/Users/chris/.gnupg/secring.gpg"
                  (java.io.File.)
                  (java.io.FileInputStream.)
                  (BcPGPSecretKeyRingCollection.)))
  
  (.encode secret (java.io.FileOutputStream. "hello.gpg"))
  
  (-> (.getKeyRings secret)
      (iterator-seq)
      (first)
      (.getSecretKey)
      
      ;;(.extractPrivateKey (char-array 0) "BC")
      )

  ("buildSecretKeyPacket"
   "certifiedPublicKey" "checksum" "copyWithNewPassword" "encode" "extractKeyData" "extractPrivateKey" "getDValue" "getEncoded" "getKeyEncryptionAlgorithm" "getKeyID" "getPublicKey" "getS2K" "getS2KUsage" "getUserAttributes" "getUserIDs" "isMasterKey" "isPrivateKeyEmpty" "isSigningKey" "new" "parseSecretKeyFromSExpr" "pub" "replacePublicKey" "secret")
  
  )
