
(ns app.comp.graph
  (:require-macros (respo.macros :refer (defcomp)))
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [div button]]
            (respo-ui.style :as ui)
            (respo.comp.text :refer (comp-text))
            (app.comp.dep-node :refer (comp-dep-node))))

(def style-graph {:background-color (hsl 0 0 0), :overflow :auto})

(def style-toolbar {:padding 16})

(defn on-load [e dispatch!] (dispatch! :graph/load-graph nil))

(defcomp
 comp-graph
 (store)
 (div
  {:style (merge ui/fullscreen style-graph)}
  (div
   {:style style-toolbar}
   (div {} (button {:inner-text "Generate!", :style ui/button, :event {:click on-load}}))
   (div
    {}
    (let [tree (get-in store [:graph :tree])]
      (if (some? tree) (comp-dep-node tree) (comp-text "Need to generate...")))))))
