
(ns stack-editor.comp.workspace
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div]]
            [respo.comp.debug :refer [comp-debug]]
            [respo-ui.style :as ui]
            [stack-editor.comp.hot-corner :refer [comp-hot-corner]]
            [stack-editor.comp.stack :refer [comp-stack]]
            [cirru-editor.comp.editor :refer [comp-editor]]
            [stack-editor.util.keycode :as keycode]
            [cirru-editor.util.dom :refer [focus!]]
            [stack-editor.actions :refer [submit-collection!]]))

(defn on-update [snapshot dispatch!]
  (dispatch! :collection/write snapshot))

(defn on-command [store]
  (fn [snapshot dispatch! e]
    (let [code (:key-code e) event (:original-event e)]
      (cond
        (= code keycode/key-d) (do
                                 (.preventDefault event)
                                 (dispatch! :stack/goto-definition nil)
                                 (focus!))
        (= code keycode/key-b) (do
                                 (.preventDefault event)
                                 (dispatch! :stack/go-back nil)
                                 (focus!))
        (= code keycode/key-s) (do
                                 (.preventDefault event)
                                 (submit-collection!
                                   (:collection store)
                                   dispatch!))
        :else nil))))

(defn render [store]
  (fn [state mutate!]
    (let [router (:router store)
          writer (:writer store)
          stack (get-in store [:writer :stack])
          pointer (get-in store [:writer :pointer])
          tree (get-in
                 store
                 [:collection (get writer :kind) (get stack pointer)])]
      (div
        {:style (merge ui/fullscreen ui/row)}
        (div
          {:style
           {:min-width "280px",
            :color (hsl 0 0 80),
            :background-color (hsl 0 0 0),
            :width "20%"}}
          (comp-hot-corner router)
          (comp-stack stack pointer))
        (comment
          comp-debug
          writer
          {:background-color (hsl 0 0 0), :z-index 999, :opacity 1})
        (div
          {:style (merge ui/flex)}
          (comp-editor
            {:tree tree,
             :clipboard (:clipboard writer),
             :focus (:focus writer)}
            on-update
            (on-command store)))))))

(def comp-workspace (create-comp :workspace render))
