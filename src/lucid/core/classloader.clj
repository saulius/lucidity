(ns lucid.core.classloader
  (:require [clojure.java.io :as io]
            [hara.reflect :as reflect]
            [lucid.space
             [jar :as jar]
             [file :as file]]))

(defmulti to-bytes
  "opens `.class` file from an external source
   (to-bytes \"target/classes/test/Dog.class\")
   => checks/bytes?"
  {:added "1.1"}
  (fn [x] (type x)))

(defmethod to-bytes java.io.InputStream [stream]
  (let [o (java.io.ByteArrayOutputStream.)]
       (io/copy stream o)
       (.toByteArray o)))

(defmethod to-bytes String [path]
  (to-bytes (io/input-stream path)))

(defonce ^:dynamic *class-cache*
  (reflect/apply-element clojure.lang.DynamicClassLoader "classCache" []))

(defonce ^:dynamic *rq*
  (reflect/apply-element clojure.lang.DynamicClassLoader "rq" []))

(def class-0
  (reflect/query-class ClassLoader ["defineClass0" :#]))

(defn dynamic-loader []
  (clojure.lang.DynamicClassLoader. file/*clojure-loader*))

(defn unload-class [name]
  (clojure.lang.Util/clearCache *rq* *class-cache*)
  (.remove *class-cache* name))

(defn path->classname
  "converts the path to a classname
   (path->classname \"test/Dog.class\")
   => \"test.Dog\""
  {:added "1.1"}
  [path]
  (let [path (if (.endsWith path".class")
               (subs path 0 (- (count path) 6))
               path)]
    (.replaceAll path file/*sep* ".")))

(defmulti load-class
  "loads class from an external source"
  {:added "1.1"}
  (fn [x & args] (type x)))

(defmethod load-class Class [cls]
  (.put *class-cache*
        (.getName cls) (java.lang.ref.SoftReference. cls *rq*))
  cls)

(defmethod load-class (Class/forName "[B") [bytes]
  (clojure.lang.Util/clearCache *rq* *class-cache*)
  (let [name (-> bytes
                 (clojure.asm.ClassReader.)
                 (.getClassName)
                 (path->classname))
        cls (class-0
             (cast ClassLoader (dynamic-loader)) name
             bytes (int 0) (int (count bytes)) nil)]
    (load-class cls)))

(defmethod load-class String [path & [entry-path]]
  (cond (.endsWith path ".class")
        (-> path
            (to-bytes)
            (load-class))

        (or (.endsWith path ".war")
            (.endsWith path ".jar"))
        (let [resource-name (file/resource-path entry-path)
              rt    (java.util.jar.JarFile. path)
              entry  (.getEntry rt resource-name)
              stream (.getInputStream rt entry)]
          (-> stream
              (to-bytes)
              (load-class)))))

(defmethod load-class clojure.lang.PersistentVector
  [coordinates entry-path]
  (load-class (jar/maven-file coordinates) entry-path))
