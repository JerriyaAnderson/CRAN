
(ns app.comp.def
  (:require-macros (respo.macros :refer (defcomp)))
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [div button]]
            (respo-ui.style :as ui)
            (respo.comp.text :refer (comp-text))
            (respo.comp.space :refer (comp-space))))

(defn on-view [path child-node]
  (fn [e dispatch!] (dispatch! :graph/view-path (conj path child-node))))

(def style-circular {:text-decoration :underline})

(def style-def {:color (hsl 0 0 70 0.6), :cursor :pointer, :white-space :nowrap})

(def style-external {:color (hsl 300 40 30), :font-size 12, :cursor :default})

(def style-highlight {:color :white})

(def style-count {:font-size 12, :color (hsl 0 0 100 0.2)})

(defcomp
 comp-def
 (child-node next-path next-cursor)
 (div
  {:event {:click (on-view next-path child-node)},
   :style (merge
           style-def
           (if (:external? child-node) style-external)
           (if (= child-node next-cursor) style-highlight)
           (if (:circular? child-node) style-circular))}
  (comp-text (str (:ns child-node) " / " (:def child-node)) nil)
  (comp-space 4 nil)
  (let [many-deps (count (:deps child-node))]
    (if (pos? many-deps) (comp-text many-deps style-count)))))
