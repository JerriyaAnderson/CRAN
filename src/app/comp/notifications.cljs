
(ns app.comp.notifications
  (:require-macros [respo.macros :refer [defcomp <> div span]])
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.core :refer [create-comp]]))

(defn on-click [idx] (fn [e dispatch!] (dispatch! :notification/remove-since idx)))

(def style-notification
  {:position "fixed",
   :top "8px",
   :right "8px",
   :transition "320ms",
   :line-height "32px",
   :white-space "nowrap",
   :color (hsl 0 0 100 0.5),
   :background-color (hsl 300 30 70 0.2),
   :z-index 999,
   :min-width "160px",
   :padding "0 16px",
   :cursor "pointer",
   :border-radius "2px"})

(defcomp
 comp-notifications
 (notifications)
 (div
  {}
  (->> notifications
       (map-indexed
        (fn [idx entry]
          [(first entry)
           (div
            {:style (merge style-notification {:top (str (+ 8 (* 40 idx)) "px")}),
             :event {:click (on-click idx)}}
            (<> span (last entry) nil))])))))
