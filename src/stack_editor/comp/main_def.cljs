
(ns stack-editor.comp.main-def
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div input]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [stack-editor.util.time :refer [now]]
            [stack-editor.style.widget :as widget]))

(defn on-input [mutate!] (fn [e dispatch!] (mutate! (:value e))))

(defn update-state [state new-text] new-text)

(defn init-state [& args] "")

(defn on-set-main [state]
  (fn [e dispatch!]
    (let [content state]
      (if (re-find (re-pattern "^.+/.+$") content)
        (dispatch! :collection/set-main content)
        (dispatch!
          :notification/add-one
          (str "\"" content "\" is invalid"))))))

(defn render [main-definition]
  (fn [state mutate!]
    (div
      {:style {:display "inline-block"}}
      (input
        {:style widget/input,
         :event {:input (on-input mutate!)},
         :attrs {:placeholder "main/definition", :value state}})
      (comp-space "8px" nil)
      (div
        {:style
         (merge
           widget/button
           (if (= main-definition state)
             {:background-color (hsl 200 30 30)})),
         :event {:click (on-set-main state)}}
        (comp-text "update -main" nil)))))

(def comp-main-def
 (create-comp :main-def init-state update-state render))
