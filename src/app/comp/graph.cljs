
(ns app.comp.graph
  (:require-macros (respo.macros :refer (defcomp)))
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [div button]]
            (respo-ui.style :as ui)
            (respo.comp.text :refer (comp-text))))

(def style-graph {:background-color (hsl 0 0 0)})

(def style-toolbar {:padding 16})

(defn on-load [e dispatch!] (dispatch! :graph/load-graph nil))

(defcomp
 comp-graph
 (store)
 (div
  {:style (merge ui/fullscreen style-graph)}
  (div
   {:style style-toolbar}
   (button {:inner-text "Generate!", :style ui/button, :event {:click on-load}}))
  (comp-text "Graph" nil)))
