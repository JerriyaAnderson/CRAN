
(ns app.comp.brief-file
  (:require-macros [respo.macros :refer [defcomp div <> span]])
  (:require [hsl.core :refer [hsl]]
            [respo.core :refer [create-comp]]
            [respo-ui.style :as ui]
            [respo.comp.space :refer [=<]]
            [app.style.widget :as widget]))

(def style-file {:padding "16px", :font-size 16, :line-height 1.6})

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

(defn on-remove [ns-text] (fn [e d! m!] (d! :collection/remove-file ns-text)))

(defcomp
 comp-brief-file
 (ns-text file)
 (div
  {:style style-file}
  (div
   {:style ui/row}
   (<> span ns-text nil)
   (=< 16 nil)
   (div {:inner-text "ns", :style style-link, :event {:click (on-edit-ns ns-text)}})
   (=< 16 nil)
   (div {:inner-text "procs", :style style-link, :event {:click (on-edit-procs ns-text)}})
   (=< 16 nil)
   (span
    {:inner-text "Delete",
     :style widget/clickable-text,
     :event {:click (on-remove ns-text)}}))
  (div
   {}
   (->> (:defs file)
        (sort compare)
        (map
         (fn [entry]
           (let [def-text (key entry)]
             [def-text
              (div
               {:inner-text (str def-text "↗"),
                :style style-link,
                :event {:click (on-edit-def ns-text def-text)}})])))))))
