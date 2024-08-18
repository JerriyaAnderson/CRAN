
(ns stack-editor.comp.proc
  (:require [respo.alias :refer [create-comp div input]]
            [hsl.core :refer [hsl]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [stack-editor.style.widget :as widget]))

(defn init-state [] "")

(defn update-state [state text] text)

(defn on-input [mutate!] (fn [e dispatch!] (mutate! (:value e))))

(defn on-click [state mutate!]
  (fn [e dispatch!]
    (dispatch! :collection/add-namespace state)
    (mutate! "")))

(defn render []
  (fn [state mutate!]
    (div
      {}
      (input
        {:style widget/input,
         :event {:input (on-input mutate!)},
         :attrs {:placeholder "", :value state}})
      (comp-space "8px" nil)
      (div
        {:style widget/button,
         :event {:click (on-click state mutate!)}}
        (comp-text "add" nil)))))

(def comp-proc (create-comp :proc init-state update-state render))
