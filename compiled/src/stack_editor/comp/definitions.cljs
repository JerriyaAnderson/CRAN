
(ns stack-editor.comp.definitions
  (:require [hsl.core :refer [hsl]]
            [clojure.string :as string]
            [respo.alias :refer (create-comp div input)]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.debug :refer [comp-debug]]
            [respo-ui.style :as ui]
            [stack-editor.util.time :refer [now]]
            [stack-editor.comp.main-def :refer [comp-main-def]]
            [respo-border.transform.space :refer [interpose-spaces]]
            [stack-editor.style.widget :as widget]
            [cirru-editor.util.dom :refer [focus!]]
            [stack-editor.comp.define :refer [comp-define]]))

(defn on-add-definition [state mutate!]
  (fn [e dispatch!]
    (let [content state]
      (if (re-find (re-pattern "^.+/.+$") content)
        (do
          (dispatch! :collection/add-definition content)
          (mutate! ""))
        (dispatch!
          :notification/add-one
          (str "\"" content "\" is not valid!"))))))

(defn on-edit-definition [definition-path]
  (fn [e dispatch!]
    (dispatch! :collection/edit-definition definition-path)
    (focus!)))

(defn render [definitions]
  (fn [state mutate!]
    (let [grouped (->>
                    definitions
                    (group-by
                      (fn [entry]
                        (let [path (first entry)
                              ns-name (first
                                        (string/split
                                          path
                                          (re-pattern "/")))]
                          ns-name)))
                    (into {}))]
      (div
        {:style (merge ui/flex ui/card {})}
        (comp-space nil "16px")
        (interpose-spaces
          (div
            {}
            (->>
              grouped
              (map
                (fn [entry]
                  (let [ns-name (first entry) def-codes (val entry)]
                    [ns-name
                     (div
                       {}
                       (comp-define ns-name)
                       (interpose-spaces
                         (div
                           {}
                           (->>
                             def-codes
                             (map
                               (fn 
                                 [code-entry]
                                 (let 
                                   [path (first code-entry)
                                    var-part
                                    (last
                                      (string/split
                                        path
                                        (re-pattern "/")))]
                                   [var-part
                                    (div
                                      {:style
                                       {:color (hsl 0 0 100),
                                        :cursor "pointer",
                                        :display "inline-block"},
                                       :event
                                       {:click
                                        (on-edit-definition path)}}
                                      (comp-text var-part nil))])))))
                         {:width "16px",
                          :display "inline-block"}))])))))
          {:height "32px"})))))

(def comp-definitions (create-comp :definitions render))
