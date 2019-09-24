(ns
  ^{:author "AlaskanEmily"
        :doc "Shared UI Components of Rogue Tyler"}
  rogue-ui-utils
  (:require [clojure.core.reducers :as reduce])
  (:import
    (java.awt Frame Panel FlowLayout Image ScrollPane Canvas Color Dimension)
    (java.awt.event WindowAdapter WindowEvent MouseAdapter)
    (java.util.concurrent.atomic AtomicInteger)))

(definterface Selectable
  (^boolean getState[])
  (^boolean setState[]))

(def window-close-handler
  (proxy [WindowAdapter] []
    (windowClosing [e]
      (.dispose (.getWindow e)))))

; Implements a panel which acts like a radio button, of which only one in a
; group can be selected at once.
(defn image-tile [selector hover scale id image w h]
  (let [
      scaled-w (* w scale)
      scaled-h (* h scale)
      scaled-image (.getScaledInstance image scaled-w scaled-h Image/SCALE_FAST)
      check-box (proxy [Canvas Selectable] []
        ; Pass the tile identifier through.
        (getTileIdentifier [] id)
        
        (setState [] (.getAndSet selector id))
        (getState [] (= (.get selector) id))
        
        ; Override paint to just draw our image and a border
        (paint [gfx]
          (.drawImage gfx scaled-image 0 0 this)
          ; Set the current color for drawing the regular border,
          ; and check if we want to draw an extended border for selection.
          (if (.getState this)
            (do
              ; Selected chooses blue
              (.setColor gfx Color/CYAN)
              ; Draw the thicker portion of the border.
              (.drawRect gfx 1 1 (- scaled-w 3) (- scaled-h 3)))
            ; Else, Unselected chooses gray (darker if this is hovered over)
            (if (= (.get hover) id)
              (.setColor gfx Color/GRAY)
              (.setColor gfx Color/LIGHT_GRAY)))
          ; Draw the regular border.
          (.drawRect gfx 0 0 (- scaled-w 1) (- scaled-h 1))))]
    (doto check-box
      
      ; Create a mouse listener which will manage the visualization of
      ; mouse-over and selection management.
      (.addMouseListener (proxy [MouseAdapter] []
        (mouseReleased [e]
          (.set selector id)
          ; Redraw all the elements. This is kind of overkill, but it's much
          ; simpler to implement, and this happens pretty rarely.
          (doseq [c (seq (.getComponents (.getParent check-box)))]
            (.repaint c)))
        (mouseExited [e]
          ; Only unset the hover variable if we were the one in it before
          (.compareAndSet hover id -1)
          (.repaint check-box))
        (mouseEntered [e]
          (.set hover id)
          (.repaint check-box))))
      
      ; Setup the size of the selector
      (.setMinimumSize (proxy [Dimension] [scaled-w scaled-h]))
      (.setPreferredSize (proxy [Dimension] [scaled-w scaled-h]))
      (.setBounds 0 0 scaled-w scaled-h))
    check-box))

; reducer to use to generate the tile ui
(defn add-image-tile [panel selector hover w h index image]
  (.add panel (image-tile selector hover 2 index image w h))
  (+ 1 index))

; Creates a tile-chooser frame. This has its own scrolling and selection system.
(defn image-chooser [images image-w image-h x y w h]
  (let [
      scroll-pane (new ScrollPane)
      panel (new Panel)
      hover (new AtomicInteger -1)
      selector (new AtomicInteger)]
    
    ; Setup the panel which will hold our tile images
    (let [flow-layout (new FlowLayout)]
      
      ; Setup the layout manager
      (doto flow-layout
        (.setAlignment FlowLayout/LEFT)
        (.setHgap 0)
        (.setVgap 0))
      
      ; Apply layout to the panel
      (doto panel
        (.setMaximumSize (proxy [Dimension] [w (+ h 10000)]))
        (.setMinimumSize (proxy [Dimension] [w h]))
        (.setBounds 4 4 w h)
        (.setLayout flow-layout)))
    ; Add the tile images
    (reduce/reduce (partial add-image-tile panel selector hover image-w image-h) 0 images)
    
    ; Setup the scroll pane
    (.doLayout panel)
    
    ; Set up the scroll-pane with our panel of images.
    (doto scroll-pane
      (.setBounds x y w h)
      (.add panel)
      (.doLayout))
    scroll-pane))

