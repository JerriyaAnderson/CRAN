
(ns stack-editor.comp.hot-corner
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div span]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [respo-ui.style :as ui]))

(defn on-switch [router-name]
  (fn [e dispatch!]
    (if (= :analyzer router-name)
      (dispatch! :router/route {:name :workspace, :data nil})
      (dispatch! :router/route {:name :analyzer, :data :definitions}))))

(defn render [router]
  (fn [state mutate!]
    (div
      {:style
       {:text-align "center",
        :font-size "24px",
        :font-weight "lighter"}}
      (case
        (:name router)
        :analyzer
        (comp-text "Analyzer" {:font-family "Helverica Neue"})
        :workspace
        (comp-text "Workspace" {:font-family "Helverica Neue"})
        nil)
      (comp-space "8px" nil)
      (div
        {:style
         (merge
           ui/button
           {:font-size "10px",
            :width "30px",
            :display "inline-block"}),
         :event {:click (on-switch (:name router))}}
        (comp-text "swtich" nil)))))

(def comp-hot-corner (create-comp :hot-corner render))
