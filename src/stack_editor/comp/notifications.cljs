
(ns stack-editor.comp.notifications
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div]]
            [respo.comp.text :refer [comp-text]]))

(defn on-click [idx] (fn [e dispatch!] (dispatch! :notification/remove-since idx)))

(def style-notification
  {:position "fixed",
   :top "8px",
   :right "8px",
   :transition "320ms",
   :line-height "32px",
   :white-space "nowrap",
   :color (hsl 0 0 100),
   :background-color (hsl 300 30 70 0.3),
   :z-index 999,
   :min-width "160px",
   :padding "0 16px",
   :cursor "pointer",
   :border-radius "2px"})

(def comp-notifications
  (create-comp
   :notifications
   (fn [notifications]
     (fn [cursor]
       (div
        {}
        (->> notifications
             (map-indexed
              (fn [idx entry]
                [(first entry)
                 (div
                  {:style (merge style-notification {:top (str (+ 8 (* 40 idx)) "px")}),
                   :event {:click (on-click idx)}}
                  (comp-text (last entry) nil))]))))))))
