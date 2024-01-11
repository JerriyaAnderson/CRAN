
(ns stack-editor.comp.hot-corner
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div span]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [respo-ui.style :as ui]))

(defn on-switch [router]
  (fn [e dispatch!]
    (dispatch! :router/route {:name :analyzer, :data (:data router)})))

(defn render [router]
  (fn [state mutate!]
    (div
      {:style
       {:text-align "center",
        :font-size "24px",
        :font-weight "lighter",
        :cursor "pointer"},
       :event {:click (on-switch router)}}
      (comp-text "Analyzer" {:font-family "Helverica Neue"}))))

(def comp-hot-corner (create-comp :hot-corner render))
