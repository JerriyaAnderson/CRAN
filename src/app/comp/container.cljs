
(ns app.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo.comp.inspect :refer [comp-inspect]]
            [respo.core :refer [defcomp div >> <> span input]]
            [respo-ui.core :as ui]
            [app.comp.loading :refer [comp-loading]]
            [app.comp.workspace :refer [comp-workspace]]
            [app.comp.notifications :refer [comp-notifications]]
            [app.comp.palette :refer [comp-palette]]
            [app.comp.modal-stack :refer [comp-modal-stack]]
            [app.comp.graph :refer [comp-graph]]
            [app.util.keycode :as keycode]
            [app.util.dom :as dom]
            [app.style.widget :as widget]
            [app.comp.file-tree :refer [comp-file-tree]]))

(defcomp
 comp-tab
 (title code selected?)
 (div
  {:style (merge
           {:font-family ui/font-fancy,
            :font-size 18,
            :font-weight 300,
            :min-width 60,
            :color (hsl 0 0 50),
            :cursor :pointer}
           (if selected? {:color (hsl 0 0 100)})),
   :on-click (fn [e d!] (d! :router/route {:name code}))}
  (<> title)))

(defcomp
 comp-container
 (store)
 (let [router (:router store), states (:states store), page (:name router)]
   (if (= :router (:name router))
     (comp-loading)
     (div
      {:style (merge
               ui/global
               ui/fullscreen
               ui/column
               {:background-color (hsl 0 0 0), :color (hsl 0 0 70)})}
      (div
       {:style (merge ui/row-middle {:padding "0 8px"})}
       (comp-tab "Files" :file-tree (= page :file-tree))
       (comp-tab "Editor" :workspace (= page :workspace))
       (comp-tab "Graph" :graph (= page :graph)))
      (case (:name router)
        :workspace (comp-workspace store)
        :graph (comp-graph store)
        :file-tree (comp-file-tree (>> states :file-tree) store)
        (<> (str router) nil))
      (comp-notifications (:notifications store))
      (comment
       comp-inspect
       "Store"
       store
       {:bottom 0, :background-color (hsl 0 0 0), :opacity 1, :color :white})
      (if (:show-palette? router)
        (comp-palette (>> states :palette) (:files (:collection store))))
      (comp-modal-stack states (:modal-stack store))))))
