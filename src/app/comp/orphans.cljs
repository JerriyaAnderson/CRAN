
(ns app.comp.orphans
  (:require-macros (respo.macros :refer (defcomp)))
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [div]]
            [respo.comp.text :refer [comp-text]]
            [respo-ui.style :as ui]))

(def style-container
  {:width 800, :height 400, :overflow :auto, :padding 16, :background (hsl 0 0 0 0.9)})

(def style-def {:min-width 200, :display :inline-block, :cursor :pointer})

(def style-title {:font-size 24, :font-weight 100, :font-family "Josefin Sans"})

(defn on-edit [def-info]
  (fn [e dispatch!]
    (dispatch!
     :collection/edit
     {:kind :defs, :ns (:ns def-info), :extra (:def def-info), :focus [1]})
    (dispatch! :modal/recycle nil)))

(defcomp
 comp-orphans
 (orphans)
 (div
  {:style style-container}
  (div {} (comp-text "Orphans:" style-title))
  (div
   {}
   (->> orphans
        (map
         (fn [def-info]
           (let [def-id (str (:ns def-info) "/" (:def def-info))]
             [def-id
              (div
               {:inner-text def-id, :style style-def, :event {:click (on-edit def-info)}})])))))))
