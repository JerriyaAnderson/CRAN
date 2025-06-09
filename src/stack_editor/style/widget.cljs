
(ns stack-editor.style.widget (:require [hsl.core :refer [hsl]] [respo-ui.style :as ui]))

(def entry
  {:color (hsl 0 0 100),
   :background-color (hsl 200 10 40 0),
   :cursor "pointer",
   :padding "0 8px",
   :display "inline-block",
   :margin-bottom "8px"})

(def var-entry
  {:min-width "160px",
   :color (hsl 0 0 80),
   :font-size "15px",
   :cursor "pointer",
   :display "inline-block",
   :font-family "Menlo,monospace"})

(def entry-line (merge var-entry {:display "block"}))

(def input
  (merge
   ui/input
   {:color (hsl 0 0 100),
    :background-color (hsl 0 0 100 0.14),
    :width "320px",
    :font-family "Menlo,monospace"}))

(def button
  (merge ui/button {:color (hsl 0 0 100 0.6), :background-color (hsl 0 0 100 0.2)}))
