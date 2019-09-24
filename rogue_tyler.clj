(ns
  ^{:author "AlaskanEmily"
  :doc "Rogue Tyler map editor"}
  rogue-tyler
  (:require [clojure.core.reducers :as reduce])
  (:import
    ; (mercury brush)
    (javax.imageio ImageIO)
    (java.io BufferedReader File FileReader InputStream InputStreamReader FileInputStream PushbackReader)
    (java.awt Frame Canvas Image FlowLayout)
    (java.awt.event WindowAdapter WindowEvent)))

(load-file "./titania_map.clj")
(load-file "./data.json/json.clj")

; Read a file by path to using data.json
(defn read-file-to-json [path]
  (let [stream (InputStreamReader. (FileInputStream. "images.json"))]
    (clojure.data.json/read stream)))

(defn image-iter [seq path]
  (cons (ImageIO/read (File. (clojure.string/join ["images/" path]))) seq))

; TEST! 
(comment brush/CreateBrush 0 0)

(defn -main []
  (println "Hello world!")
  (println )
  (let [
        ; The window to build.
        frame (Frame.)
        ; Load the test images.
        images (reduce/reduce image-iter (empty []) (read-file-to-json "images.json"))]
    (doto frame
      (.setSize 300 300)
      (.setLayout nil) ; (new FlowLayout)
      (.add (titania-map/tile-chooser images 16 16 10 32 280 260))
      (.setVisible true)
      )))

(-main)
