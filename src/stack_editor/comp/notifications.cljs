
(ns stack-editor.comp.notifications
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div]]
            [respo.comp.text :refer [comp-text]]))

(defn on-click [idx] (fn [e dispatch!] (dispatch! :notification/remove-since idx)))

(def style-notification
  {:line-height "32px",
   :min-width "160px",
   :color (hsl 0 0 100),
   :white-space "nowrap",
   :transition "320ms",
   :top "8px",
   :background-color (hsl 300 30 70 0.3),
   :cursor "pointer",
   :z-index 999,
   :padding "0 16px",
   :right "8px",
   :position "fixed",
   :border-radius "2px"})

(defn render [notifications]
  (fn [state mutate!]
    (div
     {}
     (->> notifications
          (map-indexed
           (fn [idx entry]
             [(first entry)
              (div
               {:style (merge style-notification {:top (str (+ 8 (* 40 idx)) "px")}),
                :event {:click (on-click idx)}}
               (comp-text (last entry) nil))]))))))

(def comp-notifications (create-comp :notifications render))
