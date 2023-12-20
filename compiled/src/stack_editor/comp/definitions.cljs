
(ns stack-editor.comp.definitions
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer (create-comp div input)]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.debug :refer [comp-debug]]
            [respo-ui.style :as ui]
            [stack-editor.util.time :refer [now]]
            [stack-editor.comp.main-def :refer [comp-main-def]]
            [respo-border.transform.space :refer [interpose-spaces]]))

(defn init-state [& args] "")

(defn update-state [state new-text] new-text)

(defn on-input [mutate!] (fn [e dispatch!] (mutate! (:value e))))

(defn on-add-definition [state mutate!]
  (fn [e dispatch!]
    (let [content state]
      (if (re-find (re-pattern "^.+/.+$") content)
        (do
          (dispatch! :collection/add-definition content)
          (mutate! ""))
        (dispatch!
          :notification/add-one
          [(now) (str "\"" content "\" is not valid!")])))))

(defn on-edit [definition-path]
  (fn [e dispatch!] (dispatch! :collection/edit definition-path)))

(defn render [definitions main-definition]
  (fn [state mutate!]
    (div
      {:style
       (merge ui/flex ui/card {:background-color (hsl 0 80 96)})}
      (div
        {}
        (input
          {:style (merge ui/input {:width "320px"}),
           :event {:input (on-input mutate!)},
           :attrs {:placeholder "namespace/definition", :value state}})
        (comp-space "8px" nil)
        (div
          {:style
           (merge ui/button {:line-height 2.2, :padding "0 8px"}),
           :event {:click (on-add-definition state mutate!)}}
          (comp-text "add definition"))
        (comp-space "40px" nil)
        (comp-main-def (or main-definition "")))
      (comp-space nil "16px")
      (interpose-spaces
        (div
          {}
          (->>
            definitions
            (map-indexed
              (fn [idx entry] [idx
                               (div
                                 {:style
                                  {:background-color (hsl 200 90 90),
                                   :cursor "pointer",
                                   :padding "0 8px",
                                   :display "inline-block"},
                                  :event
                                  {:click (on-edit (first entry))}}
                                 (comp-text (first entry) nil))]))))
        {:width "8px", :display "inline-block"}))))

(def comp-definitions
 (create-comp :definitions init-state update-state render))
