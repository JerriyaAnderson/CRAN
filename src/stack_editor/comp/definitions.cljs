
(ns stack-editor.comp.definitions
  (:require [hsl.core :refer [hsl]]
            [clojure.string :as string]
            [respo.alias :refer [create-comp div input]]
            [respo.cursor :refer [with-cursor]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.debug :refer [comp-debug]]
            [respo-ui.style :as ui]
            [respo-border.transform.space :refer [interpose-spaces]]
            [stack-editor.style.widget :as widget]
            [cirru-editor.util.dom :refer [focus!]]
            [stack-editor.comp.define :refer [comp-define]]
            [stack-editor.comp.ns-creator :refer [comp-ns-creator]]))

(def style-list {:max-height 240, :overflow "auto"})

(defn by-ns-part [entry]
  (let [path (first entry), ns-name (first (string/split path (re-pattern "/")))] ns-name))

(defn on-edit-definition [ns-name definition-path]
  (fn [e dispatch!] (dispatch! :collection/edit [ns-name :defs definition-path]) (focus!)))

(def style-file {:display :inline-block, :vertical-align :top, :width 240, :margin-top 16})

(defn by-var-part [code-entry]
  (let [path (first code-entry)] (last (string/split path "/"))))

(defn render [states sepal-data]
  (fn [cursor]
    (let [files (:files sepal-data)]
      (div
       {:style (merge ui/fullscreen ui/column ui/card {:background-color :black})}
       (comp-space nil "16px")
       (with-cursor
        :ns-creator
        (comp-ns-creator (:ns-creator states) (:package sepal-data)))
       (comp-space nil "32px")
       (div
        {:style (merge ui/flex {:overflow "auto", :padding-bottom 200})}
        (->> files
             (sort-by first)
             (map
              (fn [entry]
                (let [ns-name (first entry), def-codes (:defs (val entry))]
                  [ns-name
                   (div
                    {:style style-file}
                    (with-cursor ns-name (comp-define (get states ns-name) ns-name))
                    (div
                     {:style style-list}
                     (->> def-codes
                          (sort-by by-var-part)
                          (map
                           (fn [code-entry]
                             (let [path (first code-entry)
                                   var-part (last (string/split path (re-pattern "/")))]
                               [var-part
                                (div
                                 {:style widget/var-entry,
                                  :event {:click (on-edit-definition ns-name path)},
                                  :attrs {:inner-text var-part}})]))))))])))))))))

(def comp-definitions (create-comp :definitions render))
