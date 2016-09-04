(ns hara.io.file.basic)

(defn directory?
  [path]
  (.isDirectory (file path)))

(defn exists?
  [path]
  (.exists (file path)))

(defn absolute?
  [path]
  (.isAbsolute (file path)))

(defn executable?
  [path]
  (.canExecute (file path)))

(defn readable?
  [path]
  (.canRead (file path)))

(defn writeable?
  [path]
  (.canWrite (file path)))

(defn file?
  [path]
  (.isFile (file path)))

(defn hidden?
  [path]
  (.isHidden (file path)))

(defn parent
  [path]
  (.getParentFile (file path)))

(defn last-modified
  [path]
  (.lastModified (file path)))

(defn size
  [path]
  (.length (file path)))