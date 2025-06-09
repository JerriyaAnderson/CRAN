
(ns stack-editor.comp.procedures
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div input]]
            [respo-ui.style :as ui]
            [respo-border.transform.space :refer [interpose-spaces]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.debug :refer [comp-debug]]
            [stack-editor.style.widget :as widget]
            [cirru-editor.util.dom :refer [focus!]]))

(defn on-input [mutate!] (fn [e dispatch!] (mutate! (:value e))))

(defn on-add-procedure [mutate! procedure]
  (fn [e dispatch!] (dispatch! :collection/add-procedure procedure) (mutate! "")))

(defn update-state [state text] text)

(defn on-edit-procedure [procedure]
  (fn [e dispatch!] (dispatch! :collection/edit [:procedures procedure]) (focus!)))

(defn init-state [& args] "")

(defn render [procedures]
  (fn [state mutate!]
    (div
     {:style (merge ui/flex ui/card)}
     (div
      {}
      (input
       {:style widget/input,
        :event {:input (on-input mutate!)},
        :attrs {:placeholder "namespace", :value state}})
      (comp-space "8px" nil)
      (div
       {:style widget/button, :event {:click (on-add-procedure mutate! state)}}
       (comp-text "add" nil)))
     (comp-space nil "16px")
     (interpose-spaces
      (div
       {}
       (->> procedures
            (sort-by first)
            (map
             (fn [entry]
               (let [ns-name (first entry)]
                 [ns-name
                  (div
                   {:style widget/entry-line, :event {:click (on-edit-procedure ns-name)}}
                   (comp-text ns-name nil))])))))
      {:height "8px"})
     (comment comp-debug procedures nil))))

(def comp-procedures (create-comp :procedures init-state update-state render))
