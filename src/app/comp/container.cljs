
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
 comp-container
 (store)
 (let [router (:router store), states (:states store)]
   (div
    {:tab-index 0,
     :style (merge ui/global {:background-color (hsl 0 0 0), :color (hsl 0 0 70)})}
    (case (:name router)
      :loading (comp-loading)
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
    (comp-modal-stack states (:modal-stack store)))))
