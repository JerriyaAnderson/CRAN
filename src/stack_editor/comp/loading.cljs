
(ns stack-editor.comp.loading
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div]]
            [respo.comp.text :refer [comp-text]]
            [respo-ui.style :as ui]))

(defn render [store]
  (fn [state mutate!]
    (div
     {:style (merge
              ui/fullscreen
              ui/row-center
              {:color (hsl 0 0 100),
               :font-size "20px",
               :font-weight "light",
               :background-color (hsl 200 40 10),
               :justify-content "center"})}
     (comp-text "Loading" nil))))

(def comp-loading (create-comp :loading render))
