
(ns stack-editor.comp.define
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div input]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.debug :refer [comp-debug]]
            [respo-ui.style :as ui]
            [stack-editor.style.widget :as widget]
            [stack-editor.util.keycode :as keycode]))

(defn on-input [mutate!] (fn [e dispatch!] (mutate! (:value e))))

(defn update-state [state text] text)

(def style-namespace {:cursor "pointer", :display "inline-block"})

(defn on-click [ns-text]
  (fn [e dispatch!] (dispatch! :collection/edit [:namespaces ns-text])))

(def style-ns {:white-space :nowrap, :font-family "Source Code Pro,Menlo,monospace"})

(defn init-state [& args] "")

(defn on-keydown [text mutate! ns-name]
  (fn [e dispatch!]
    (if (and (= keycode/key-enter (:key-code e)) (pos? (count text)))
      (do (mutate! "") (dispatch! :collection/add-definition (str ns-name "/" text))))))

(defn render [ns-name]
  (fn [state mutate!]
    (div
     {}
     (div
      {:style style-namespace, :event {:click (on-click ns-name)}}
      (comp-text ns-name style-ns)
      (comp-space "8px" nil))
     (div
      {}
      (input
       {:style (merge (merge widget/input {:width "200px", :height "24px"})),
        :event {:keydown (on-keydown state mutate! ns-name), :input (on-input mutate!)},
        :attrs {:placeholder "", :value state}})))))

(def comp-define (create-comp :define init-state update-state render))
