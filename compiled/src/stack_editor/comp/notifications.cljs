
(ns stack-editor.comp.notifications
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div]]
            [respo.comp.text :refer [comp-text]]))

(def style-notification
 {:line-height 3,
  :color (hsl 0 0 80),
  :transition "300ms",
  :top "16px",
  :background-color (hsl 200 30 30),
  :width "320px",
  :cursor "pointer",
  :z-index 999,
  :padding "0 16px",
  :right "16px",
  :position "fixed"})

(defn on-click [notification-id]
  (fn [e dispatch!]
    (dispatch! :notification/remove-one notification-id)))

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
                                {:top (str (* 50 idx) "px")}),
                              :event {:click (on-click (first entry))}}
                             (comp-text (last entry) nil))]))))))

(def comp-notifications (create-comp :notifications render))
