
(ns app.comp.loading
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div]]
            [respo.comp.text :refer [comp-text]]
            [respo-ui.style :as ui]))

(def style-loading
  {:background-color (hsl 200 40 10),
   :justify-content "center",
   :color (hsl 0 0 80),
   :font-size "32px",
   :font-weight "100",
   :font-family "Josefin Sans"})

(def comp-loading
  (create-comp
   :loading
   (fn []
     (fn [cursor]
       (div
        {:style (merge ui/fullscreen ui/row-center style-loading)}
        (comp-text "Loading..." nil))))))
