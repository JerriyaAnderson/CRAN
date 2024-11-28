
(ns stack-editor.comp.notifications
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div]]
            [respo.comp.text :refer [comp-text]]))

(defn on-click [idx]
  (fn [e dispatch!] (dispatch! :notification/remove-since idx)))

(def style-notification
 {:line-height "32px",
  :color (hsl 0 0 80),
  :white-space "nowrap",
  :transition "320ms",
  :top "8px",
  :background-color (hsl 200 30 30 0.6),
  :width "320px",
  :cursor "pointer",
  :z-index 999,
  :padding "0 8px",
  :right "8px",
  :position "fixed"})

(defn render [notifications]
  (fn [state mutate!]
    (div
      {}
      (->>
        notifications
        (map-indexed
          (fn [idx entry] [(first entry)
                           (div
                             {:style
                              (merge
                                style-notification
                                {:top (str (+ 8 (* 40 idx)) "px")}),
                              :event {:click (on-click idx)}}
                             (comp-text (last entry) nil))]))))))

(def comp-notifications (create-comp :notifications render))
