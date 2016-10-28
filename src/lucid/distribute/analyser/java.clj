(ns lucid.distribute.analyser.java
  (:require [lucid.distribute.analyser.base :as analyser]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(defn get-class
  "grabs the symbol of the class in the java file
   (get-class (io/file \"example/repack.advance/java/im/chit/repack/common/Hello.java\"))
   => 'im.chit.repack.common.Hello"
  {:added "1.2"} [file]
  (let [pkg (-> (->> (io/reader file)
                     (line-seq)
                     (filter #(.startsWith % "package") )
                     (first))
                (string/split #"[ ;]")
                (second))
        nm  (let [nm (.getName file)]
              (subs nm 0 (- (count nm) 5)))]
    (symbol (str pkg "." nm))))

(defn get-imports
  "grabs the symbol of the class in the java file
   (get-imports (io/file \"example/repack.advance/java/im/chit/repack/common/Hello.java\"))
   => ()
 
   (get-imports (io/file \"example/repack.advance/java/im/chit/repack/web/Client.java\"))
   => '(im.chit.repack.common.Hello)"
  {:added "1.2"} [file]
  (->> (io/reader file)
       (line-seq)
       (filter #(.startsWith % "import") )
       (map #(string/split % #"[ ;]"))
       (map second)
       (map symbol)))

(defmethod analyser/file-info :java [file]
  {:file file
   :exports #{[:class (get-class file)]}
   :imports (set (map (fn [jv] [:class jv]) (get-imports file)))})
