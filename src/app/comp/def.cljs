
(ns app.comp.def
  (:require [hsl.core :refer [hsl]]
            [respo.core :refer [defcomp div <> span input]]
            [respo-ui.core :as ui]
            [respo.comp.space :refer [=<]]))

(defn on-view [path child-node]
  (fn [e dispatch!]
    (if (.-metaKey (:original-event e))
      (dispatch!
       :collection/edit
       {:kind :defs, :ns (:ns child-node), :extra (:def child-node), :focus []})
      (dispatch!
       :graph/view-path
       (conj path {:ns (:ns child-node), :def (:def child-node)})))))

(def style-circular {:text-decoration :underline})

(def style-def
  {:color (hsl 0 0 70 0.7), :font-size 14, :cursor :pointer, :white-space :nowrap})

(def style-external {:color (hsl 260 16 44), :font-size 12, :cursor :default})

(def style-highlight {:color (hsl 0 0 100 0.86)})

(def style-count {:font-size 12, :color (hsl 0 0 100 0.4)})

(defcomp
 comp-def
 (child-node path selected?)
 (div
  {:style (merge
           style-def
           (if (:external? child-node) style-external)
           (if selected? style-highlight)
           (if (:circular? child-node) style-circular)),
   :on-click (on-view path child-node)}
  (<> span (str (:ns child-node) " / " (:def child-node)) nil)
  (=< 4 nil)
  (let [many-deps (count (:deps child-node))]
    (if (pos? many-deps) (<> span many-deps style-count)))))
