
(ns stack-editor.comp.define
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div input]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.debug :refer [comp-debug]]
            [respo-ui.style :as ui]
            [stack-editor.style.widget :as widget]))

(defn on-input [mutate!] (fn [e dispatch!] (mutate! (:value e))))

(defn on-add [text mutate! ns-name]
  (fn [e dispatch!]
    (if (> (count text) 0)
      (do (mutate! "") (dispatch! :collection/add-definition (str ns-name "/" text))))))

(defn update-state [state text] text)

(def style-namespace {:cursor "pointer", :display "inline-block"})

(defn on-click [ns-text]
  (fn [e dispatch!] (dispatch! :collection/edit [:namespaces ns-text])))

(defn on-proc [ns-name]
  (fn [e dispatch!] (dispatch! :collection/edit [:procedures ns-name])))

(defn init-state [& args] "")

(def style-proc {:line-height "24px", :width "60px", :cursor "pointer", :height "24px"})

(defn render [ns-name]
  (fn [state mutate!]
    (div
     {}
     (div
      {:style style-namespace, :event {:click (on-click ns-name)}}
      (comp-text ns-name {:font-family "Menlo,monospace"}))
     (comp-space "8px" nil)
     (input
      {:style (merge (merge widget/input {:width "200px", :height "24px"})),
       :event {:input (on-input mutate!)},
       :attrs {:placeholder "", :value state}})
     (comp-space "8px" nil)
     (div
      {:style (merge widget/button {:line-height "24px", :height "24px"}),
       :event {:click (on-add state mutate! ns-name)}}
      (comp-text "add" nil))
     (comp-space "8px" nil)
     (div
      {:style (merge widget/button style-proc), :event {:click (on-proc ns-name)}}
      (comp-text "proc" nil)))))

(def comp-define (create-comp :define init-state update-state render))
