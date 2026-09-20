
(ns stack-editor.comp.define
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div input]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.debug :refer [comp-debug]]
            [respo-ui.style :as ui]
            [stack-editor.style.widget :as widget]
            [stack-editor.util.keycode :as keycode]))

(defn on-input [cursor] (fn [e dispatch!] (dispatch! :states [cursor (:value e)])))

(defn update-state [state text] text)

(def style-namespace {:display "inline-block", :cursor "pointer"})

(defn on-click [ns-text] (fn [e dispatch!] (dispatch! :collection/edit [ns-text :ns])))

(defn on-proc [ns-name] (fn [e dispatch!] (dispatch! :collection/edit [ns-name :procs])))

(def style-ns {:font-family "Source Code Pro,Menlo,monospace", :white-space :nowrap})

(def style-proc
  {:width 48,
   :min-width 48,
   :height 24,
   :line-height "24px",
   :cursor :pointer,
   :display :inline-block,
   :text-decoration :underline})

(defn on-keydown [cursor text ns-name]
  (fn [e dispatch!]
    (if (and (= keycode/key-enter (:key-code e)) (pos? (count text)))
      (do
       (dispatch! :states [cursor ""])
       (dispatch! :collection/add-definition [ns-name text])))))

(def comp-define
  (create-comp
   :define
   (fn [states ns-name]
     (fn [cursor]
       (let [state (or (:data states) "")]
         (div
          {}
          (div
           {:style style-namespace, :event {:click (on-click ns-name)}}
           (comp-text ns-name style-ns)
           (comp-space 16 nil)
           (div
            {:style (merge style-proc), :event {:click (on-proc ns-name)}}
            (comp-text "proc" nil)))
          (div
           {}
           (input
            {:style (merge (merge widget/input {:width "200px", :height "24px"})),
             :attrs {:value state, :placeholder ""},
             :event {:input (on-input cursor), :keydown (on-keydown cursor state ns-name)}}))))))))

(defn init-state [& args] "")
