
(ns app.comp.brief-file
  (:require-macros (respo.macros :refer (defcomp)))
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [div]]
            [respo.comp.text :refer [comp-text]]
            [respo-ui.style :as ui]
            (respo.comp.space :refer (comp-space))))

(def style-file {:padding "16px", :line-height 1.4})

(defn on-edit-ns [ns-text]
  (fn [e dispatch!]
    (dispatch! :collection/edit {:kind :ns, :ns ns-text, :extra nil, :focus []})))

(defn on-edit-procs [ns-text]
  (fn [e dispatch!]
    (dispatch! :collection/edit {:kind :procs, :ns ns-text, :extra nil, :focus []})))

(def style-link {:cursor :pointer})

(defn on-edit-def [ns-text def-text]
  (fn [e dispatch!]
    (dispatch! :collection/edit {:kind :defs, :ns ns-text, :extra def-text, :focus [2]})))

(defcomp
 comp-brief-file
 (ns-text file)
 (div
  {:style style-file}
  (div
   {:style ui/row}
   (div {:inner-text "ns", :style style-link, :event {:click (on-edit-ns ns-text)}})
   (comp-space 16 nil)
   (div {:inner-text "procs", :style style-link, :event {:click (on-edit-procs ns-text)}}))
  (div
   {}
   (->> (:defs file)
        (map
         (fn [entry]
           (let [def-text (key entry)]
             [def-text
              (div
               {:inner-text def-text,
                :style style-link,
                :event {:click (on-edit-def ns-text def-text)}})])))))))
