
(ns stack-editor.style.widget
  (:require [hsl.core :refer [hsl]]))

(def entry
 {:color (hsl 0 0 100),
  :background-color (hsl 200 10 40 0),
  :cursor "pointer",
  :padding "0 8px",
  :display "inline-block",
  :margin-bottom "8px"})
