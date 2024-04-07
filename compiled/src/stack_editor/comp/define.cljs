
(ns stack-editor.comp.define
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div input]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.debug :refer [comp-debug]]
            [respo-ui.style :as ui]))

(defn init-state [& args] "")

(defn update-state [state text] text)

(defn on-input [mutate!] (fn [e dispatch!] (mutate! (:value e))))

(defn on-add [text mutate! ns-name]
  (fn [e dispatch!]
    (if (> (count text) 0)
      (do
        (mutate! "")
        (dispatch!
          :collection/add-definition
          (str ns-name "/" text))))))

(defn render [ns-name]
  (fn [state mutate!]
    (div
      {}
      (comp-text (str ns-name "/") nil)
      (comp-space "8px" nil)
      (input
        {:style
         (merge
           ui/input
           {:color (hsl 0 0 100),
            :background-color (hsl 0 0 100 0.4)}),
         :event {:input (on-input mutate!)},
         :attrs {:placeholder "var", :value state}})
      (comp-space "8px" nil)
      (div
        {:style ui/button,
         :event {:click (on-add state mutate! ns-name)}}
        (comp-text "add" nil)))))

(def comp-define (create-comp :define init-state update-state render))
