
(ns stack-editor.comp.rename-path
  (:require [respo.alias :refer [create-comp div input]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [respo-ui.style :as ui]
            [stack-editor.style.widget :as widget]))

(defn on-input [mutate!] (fn [e dispatch!] (mutate! (:value e))))

(defn update-state [state new-text] new-text)

(defn init-state [code-path] (last code-path))

(defn on-rename [code-path text]
  (fn [e dispatch!]
    (dispatch! :collection/rename [(first code-path) (last code-path) text])
    (dispatch! :modal/recycle nil)))

(defn render [code-path]
  (fn [state mutate!]
    (div
     {}
     (div {} (comp-text (str "Rename in " (first code-path)) nil))
     (div {} (comp-text (last code-path) nil))
     (div
      {}
      (input
       {:style (merge ui/input {:width 400}),
        :event {:input (on-input mutate!)},
        :attrs {:value state}})
      (comp-space 16 nil)
      (div
       {:style widget/button,
        :event {:click (on-rename code-path state)},
        :attrs {:inner-text "Rename"}})))))

(def comp-rename-path (create-comp :rename-path init-state update-state render))
