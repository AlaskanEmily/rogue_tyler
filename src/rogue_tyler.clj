(ns
  ^{:author "AlaskanEmily"
  :doc "Rogue Tyler map editor"}
  rogue-tyler
  (:require [clojure.core.reducers :as reduce] rogue-ui-utils map-window)
  (:import
    (javax.imageio ImageIO)
    (java.io BufferedReader File FileReader InputStream InputStreamReader FileInputStream PushbackReader)
    (java.awt Frame Canvas Image FlowLayout)
    (java.awt.event WindowAdapter WindowEvent)))

(load "data.json/json")

; Read a file by path to using data.json
(defn read-file-to-json [path]
  (let [stream (InputStreamReader. (FileInputStream. "images.json"))]
    (clojure.data.json/read stream)))

(defn image-iter [seq path]
  (cons (ImageIO/read (File. (clojure.string/join ["images/" path]))) seq))

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
      (.setLocation 100 100)
      (.setLayout nil) ; (new FlowLayout)
      (.add (rogue-ui-utils/image-chooser images 16 16 10 32 280 260))
      (.addWindowListener rogue-ui-utils/window-close-handler)
      (.setVisible true)
      )))

