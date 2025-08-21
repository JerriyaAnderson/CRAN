
(ns stack-editor.comp.loading
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div]]
            [respo.comp.text :refer [comp-text]]
            [respo-ui.style :as ui]))

(def style-loading
  {:color (hsl 0 0 100),
   :font-size "20px",
   :font-weight "light",
   :background-color (hsl 200 40 10),
   :justify-content "center"})

(defn render [store]
  (fn [state mutate!]
    (div {:style (merge ui/fullscreen ui/row-center style-loading)} (comp-text "Loading" nil))))

(def comp-loading (create-comp :loading render))
