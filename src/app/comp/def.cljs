
(ns app.comp.def
  (:require-macros (respo.macros :refer (defcomp)))
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [div button]]
            (respo-ui.style :as ui)
            (respo.comp.text :refer (comp-text))
            (respo.comp.space :refer (comp-space))))

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
  {:event {:click (on-view path child-node)},
   :style (merge
           style-def
           (if (:external? child-node) style-external)
           (if selected? style-highlight)
           (if (:circular? child-node) style-circular))}
  (comp-text (str (:ns child-node) " / " (:def child-node)) nil)
  (comp-space 4 nil)
  (let [many-deps (count (:deps child-node))]
    (if (pos? many-deps) (comp-text many-deps style-count)))))
