
(ns app.comp.container
  (:require-macros (respo.macros :refer (defcomp)))
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [div span]]
            [respo.cursor :refer [with-cursor]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.debug :refer [comp-debug]]
            [respo-ui.style :as ui]
            [app.comp.loading :refer [comp-loading]]
            [app.comp.workspace :refer [comp-workspace]]
            [app.comp.notifications :refer [comp-notifications]]
            [app.comp.palette :refer [comp-palette]]
            [app.comp.modal-stack :refer [comp-modal-stack]]
            (app.comp.graph :refer (comp-graph))
            [app.util.keycode :as keycode]
            [app.util.dom :as dom]
            [app.style.widget :as widget]
            (app.comp.file-tree :refer (comp-file-tree))))

(defcomp
 comp-container
 (store)
 (let [router (:router store), states (:states store)]
   (div
    {:style (merge ui/global {:background-color (hsl 0 0 0), :color (hsl 0 0 70)}),
     :attrs {:tab-index 0}}
    (case (:name router)
      :loading (comp-loading)
      :workspace (comp-workspace store)
      :graph (comp-graph store)
      :file-tree (with-cursor :file-tree (comp-file-tree (:file-tree states) store))
      (comp-text router nil))
    (comp-notifications (:notifications store))
    (comp-debug (:writer store) {:bottom 0})
    (if (:show-palette? router)
      (with-cursor :palette (comp-palette (:palette states) (:files (:collection store)))))
    (comp-modal-stack states (:modal-stack store)))))
