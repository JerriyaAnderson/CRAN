
(ns stack-editor.comp.ns-creator
  (:require [clojure.string :as string]
            [respo.alias :refer [create-comp div input]]
            [hsl.core :refer [hsl]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [stack-editor.style.widget :as widget]))

(defn on-click [cursor state]
  (fn [e dispatch!]
    (if (not (string/blank? state))
      (do (dispatch! :collection/add-namespace state) (dispatch! :states [cursor ""])))))

(defn on-input [cursor] (fn [e dispatch!] (dispatch! :states [cursor (:value e)])))

(defn on-keydown [cursor state]
  (fn [e dispatch!]
    (if (and (= 13 (:key-code e)) (not (string/blank? state)))
      (do (dispatch! :collection/add-namespace state) (dispatch! :states [cursor ""])))))

(def comp-ns-creator
  (create-comp
   :ns-creator
   (fn [states pkg]
     (fn [cursor]
       (let [state (or (:data states) "")]
         (div
          {}
          (input
           {:style widget/input,
            :event {:input (on-input cursor), :keydown (on-keydown cursor state)},
            :attrs {:value state, :placeholder (str pkg ".")}})
          (comp-space "8px" nil)
          (div
           {:style widget/button, :event {:click (on-click cursor state)}}
           (comp-text "add" nil))))))))
