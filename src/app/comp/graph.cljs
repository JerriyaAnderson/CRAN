
(ns app.comp.graph
  (:require-macros (respo.macros :refer (defcomp)))
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [div button]]
            (respo-ui.style :as ui)
            (respo.comp.text :refer (comp-text))
            (respo.comp.space :refer (comp-space))
            (app.comp.def :refer (comp-def))
            (app.util.detect :refer (def-order))
            (app.style.widget :as widget)))

(def style-body {:flex 1, :overflow :auto})

(def style-graph {:background-color (hsl 0 0 0), :overflow :auto})

(defn on-load [e dispatch!] (dispatch! :graph/load-graph nil))

(def style-toolbar {:padding 16})

(defn on-files [e dispatch!] (dispatch! :router/route {:name :file-tree, :data nil}))

(def style-column {:min-width 80, :overflow :auto, :padding "16px 16px", :flex-shrink 0})

(defn on-edit [e dispatch!] (dispatch! :graph/edit-current nil))

(defn render-toolbar []
  (div
   {:style style-toolbar}
   (div
    {}
    (button {:inner-text "Files", :style widget/button, :event {:click on-files}})
    (comp-space 8 nil)
    (button {:inner-text "Edit", :style widget/button, :event {:click on-edit}})
    (comp-space 64 nil)
    (button {:inner-text "Build tree", :style widget/button, :event {:click on-load}}))))

(defcomp
 comp-graph
 (store)
 (div
  {:style (merge ui/fullscreen ui/column style-graph)}
  (render-toolbar)
  (let [tree (get-in store [:graph :tree]), view-path (get-in store [:graph :path])]
    (div
     {:style (merge ui/row style-body)}
     (loop [children [], path view-path]
       (if (empty? path)
         children
         (let [path-cursor (first path)
               rest-path (rest path)
               next-cursor (first rest-path)
               path-length (count children)
               next-path (subvec view-path 0 (inc path-length))
               next-children (conj
                              children
                              [path-length
                               (div
                                {:style style-column}
                                (->> (:deps path-cursor)
                                     (sort def-order)
                                     (map-indexed
                                      (fn [idx child-node]
                                        [idx (comp-def child-node next-path next-cursor)]))))])]
           (recur next-children rest-path))))))))
