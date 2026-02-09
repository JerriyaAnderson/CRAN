
(ns stack-editor.comp.hot-corner
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div span]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [respo-ui.style :as ui]))

(defn on-switch [router writer]
  (fn [e dispatch!]
    (if (= (:name router) :workspace)
      (dispatch! :router/route {:name :analyzer, :data :definitions})
      (if (not (empty? (:stack writer)))
        (dispatch! :router/route {:name :workspace, :data nil})))))

(defn render [router writer]
  (fn [state mutate!]
    (div
     {:style {:font-size "24px",
              :font-weight "300",
              :text-align "center",
              :cursor "pointer"},
      :event {:click (on-switch router writer)}}
     (comp-text "Stack Editor" {:font-family "Josefin Sans"}))))

(def comp-hot-corner (create-comp :hot-corner render))
