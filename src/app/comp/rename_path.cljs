
(ns app.comp.rename-path
  (:require [respo.alias :refer [create-comp div input]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [respo-ui.style :as ui]
            [app.style.widget :as widget]))

(defn init-state [code-path]
  (let [{ns-part :ns, kind :kind, extra-name :extra} code-path]
    (if (= kind :defs) (str ns-part "/" extra-name) ns-part)))

(defn on-input [cursor] (fn [e dispatch!] (dispatch! :states [cursor (:value e)])))

(defn on-rename [code-path text]
  (fn [e dispatch!]
    (println "on-rename" code-path text)
    (dispatch! :collection/rename [code-path text])
    (dispatch! :modal/recycle nil)))

(def comp-rename-path
  (create-comp
   :rename-path
   (fn [states code-path]
     (fn [cursor]
       (let [state (or (:data states) (init-state code-path))]
         (div
          {}
          (div {} (comp-text (str "Rename in " (first code-path)) nil))
          (div {} (comp-text (last code-path) nil))
          (div
           {}
           (input
            {:style (merge ui/input {:width 400}),
             :attrs {:value state},
             :event {:input (on-input cursor)}})
           (comp-space 16 nil)
           (div
            {:style widget/button,
             :attrs {:inner-text "Rename"},
             :event {:click (on-rename code-path state)}}))))))))
