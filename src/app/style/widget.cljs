
(ns app.style.widget (:require [hsl.core :refer [hsl]] [respo-ui.core :as ui]))

(def button
  (merge
   ui/button
   {:background-color (hsl 0 0 100 0.2),
    :color (hsl 0 0 100 0.6),
    :height 28,
    :line-height "28px",
    :border :none}))

(def clickable-text
  {:text-decoration :underline,
   :cursor :pointer,
   :color (hsl 0 0 80),
   :font-family "Josefin Sans"})

(def entry
  {:display "inline-block",
   :background-color (hsl 200 10 40 0),
   :color (hsl 0 0 100),
   :padding "0 8px",
   :cursor "pointer",
   :margin-bottom "8px"})

(def var-entry
  {:color (hsl 0 0 80),
   :cursor "pointer",
   :font-family "Source Code Pro,Menlo,monospace",
   :font-size "14px",
   :line-height "24px",
   :min-width "160px"})

(def entry-line (merge var-entry {:display "block"}))

(def input
  (merge
   ui/input
   {:background-color (hsl 0 0 100 0.14),
    :color (hsl 0 0 100),
    :font-family "Source Code Pro,Menlo,monospace",
    :width "200px",
    :border :none}))
