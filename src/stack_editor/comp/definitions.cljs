
(ns stack-editor.comp.definitions
  (:require [hsl.core :refer [hsl]]
            [clojure.string :as string]
            [respo.alias :refer (create-comp div input)]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.debug :refer [comp-debug]]
            [respo-ui.style :as ui]
            [stack-editor.comp.main-def :refer [comp-main-def]]
            [respo-border.transform.space :refer [interpose-spaces]]
            [stack-editor.style.widget :as widget]
            [cirru-editor.util.dom :refer [focus!]]
            [stack-editor.comp.define :refer [comp-define]]
            [stack-editor.comp.ns-creator :refer [comp-ns-creator]]))

(defn on-edit-definition [definition-path]
  (fn [e dispatch!] (dispatch! :collection/edit [:definitions definition-path]) (focus!)))

(defn by-ns-part [entry]
  (let [path (first entry), ns-name (first (string/split path (re-pattern "/")))] ns-name))

(def style-list {:max-height 240, :overflow "auto"})

(defn by-var-part [code-entry]
  (let [path (first code-entry)] (last (string/split path "/"))))

(def style-file {:vertical-align :top, :margin-top 16, :width 240, :display :inline-block})

(defn render [definitions ns-names]
  (fn [state mutate!]
    (let [ns-base (->> ns-names (map (fn [ns-name] [ns-name {}])) (into {}))
          grouped (merge ns-base (->> definitions (group-by by-ns-part)))]
      (div
       {:style (merge ui/flex ui/column ui/card {})}
       (comp-space nil "16px")
       (comp-ns-creator (keys definitions))
       (comp-space nil "32px")
       (div
        {:style (merge ui/flex {:overflow "auto", :padding-bottom 200})}
        (->> grouped
             (sort-by first)
             (map
              (fn [entry]
                (let [ns-name (first entry), def-codes (val entry)]
                  [ns-name
                   (div
                    {:style style-file}
                    (comp-define ns-name)
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
                                  :event {:click (on-edit-definition path)},
                                  :attrs {:inner-text var-part}})]))))))])))))))))

(def comp-definitions (create-comp :definitions render))
