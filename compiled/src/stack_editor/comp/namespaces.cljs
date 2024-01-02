
(ns stack-editor.comp.namespaces
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo-border.transform.space :refer [interpose-spaces]]
            [respo.alias :refer (create-comp div input)]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [stack-editor.style.widget :as widget]))

(defn init-state [& args] "")

(defn update-state [state text] text)

(defn on-input [mutate!] (fn [e dispatch!] (mutate! (:value e))))

(defn on-add-namespace [mutate! state]
  (fn [e dispatch!]
    (dispatch! :collection/add-namespace state)
    (mutate! "")))

(defn on-edit-ns [namespace']
  (fn [e dispatch!] (dispatch! :collection/edit-namespace namespace')))

(defn render [namespaces]
  (fn [state mutate!]
    (div
      {:style (merge ui/flex ui/card)}
      (div
        {}
        (input
          {:style ui/input,
           :event {:input (on-input mutate!)},
           :attrs {:placeholder "namespace", :value state}})
        (comp-space "8px" nil)
        (div
          {:style ui/button,
           :event {:click (on-add-namespace mutate! state)}}
          (comp-text "add" nil)))
      (comp-space nil "16px")
      (interpose-spaces
        (div
          {}
          (->>
            namespaces
            (map-indexed
              (fn [idx entry] [idx
                               (div
                                 {:style widget/entry,
                                  :event
                                  {:click (on-edit-ns (first entry))}}
                                 (comp-text (first entry) nil))]))))
        {:width "8px", :display "inline-block"}))))

(def comp-namespaces
 (create-comp :namespaces init-state update-state render))
