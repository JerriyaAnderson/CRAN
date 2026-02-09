
(ns stack-editor.comp.ns-creator
  (:require [clojure.string :as string]
            [respo.alias :refer [create-comp div input]]
            [hsl.core :refer [hsl]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [stack-editor.style.widget :as widget]))

(defn on-click [state mutate!]
  (fn [e dispatch!]
    (if (not (string/blank? state))
      (do (dispatch! :collection/add-namespace state) (mutate! "")))))

(defn on-input [mutate!] (fn [e dispatch!] (mutate! (:value e))))

(defn on-keydown [state mutate!]
  (fn [e dispatch!]
    (if (and (= 13 (:key-code e)) (not (string/blank? state)))
      (do (dispatch! :collection/add-namespace state) (mutate! "")))))

(defn render [pkg]
  (fn [state mutate!]
    (div
     {}
     (input
      {:style widget/input,
       :event {:input (on-input mutate!), :keydown (on-keydown state mutate!)},
       :attrs {:value state, :placeholder (str pkg ".")}})
     (comp-space "8px" nil)
     (div
      {:style widget/button, :event {:click (on-click state mutate!)}}
      (comp-text "add" nil)))))

(defn update-state [state text] text)

(defn init-state [pkg] "")

(def comp-ns-creator (create-comp :ns-creator init-state update-state render))
