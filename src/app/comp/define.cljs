
(ns app.comp.define
  (:require-macros (respo.macros :refer (defcomp)))
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [div input]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.debug :refer [comp-debug]]
            [respo-ui.style :as ui]
            [app.style.widget :as widget]
            [app.util.keycode :as keycode]))

(defn on-input [parent-cursor]
  (fn [e dispatch!] (dispatch! :states [parent-cursor (:value e)])))

(defn update-state [state text] text)

(def style-namespace {:display "inline-block", :cursor "pointer"})

(defn on-click [ns-text]
  (fn [e dispatch!]
    (dispatch! :collection/edit {:ns ns-text, :kind :ns, :extra nil, :focus []})))

(defn on-proc [ns-name]
  (fn [e dispatch!]
    (dispatch! :collection/edit {:ns ns-name, :kind :procs, :extra nil, :focus []})))

(def style-ns {:font-family "Source Code Pro,Menlo,monospace", :white-space :nowrap})

(def style-proc
  {:width 48,
   :min-width 48,
   :height 24,
   :line-height "24px",
   :cursor :pointer,
   :display :inline-block,
   :text-decoration :underline})

(defn on-keydown [parent-cursor text ns-name]
  (fn [e dispatch!]
    (if (and (= keycode/key-enter (:key-code e)) (pos? (count text)))
      (do
       (dispatch! :states [parent-cursor ""])
       (dispatch! :collection/add-definition [ns-name text])))))

(defcomp
 comp-define
 (draft ns-name parent-cursor)
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
     :attrs {:value draft, :placeholder ""},
     :event {:input (on-input parent-cursor),
             :keydown (on-keydown parent-cursor draft ns-name)}}))))

(defn init-state [& args] "")
