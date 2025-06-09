
(ns stack-editor.comp.workspace
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.debug :refer [comp-debug]]
            [respo-ui.style :as ui]
            [stack-editor.comp.hot-corner :refer [comp-hot-corner]]
            [stack-editor.comp.stack :refer [comp-stack]]
            [cirru-editor.comp.editor :refer [comp-editor]]
            [stack-editor.util.keycode :as keycode]
            [cirru-editor.util.dom :refer [focus!]]
            [stack-editor.actions :refer [submit-collection! submit-changes!]]
            [stack-editor.util.dom :as dom]
            [stack-editor.style.widget :as widget]))

(defn on-command [store]
  (fn [snapshot dispatch! e]
    (let [code (:key-code e)
          event (:original-event e)
          command? (or (.-metaKey event) (.-ctrlKey event))
          shift? (.-shiftKey event)]
      (cond
        (= code keycode/key-d)
          (do (.preventDefault event) (dispatch! :stack/goto-definition shift?) (focus!))
        (= code keycode/key-k)
          (do (.preventDefault event) (dispatch! :stack/go-back nil) (focus!))
        (= code keycode/key-j)
          (do (.preventDefault event) (dispatch! :stack/go-next nil) (focus!))
        (= code keycode/key-s)
          (do
           (.preventDefault event)
           (if shift?
             (submit-collection! (:collection store) dispatch!)
             (submit-changes! (:collection store) dispatch!)))
        (and command? (= code keycode/key-p))
          (do
           (.preventDefault event)
           (.stopPropagation event)
           (dispatch! :router/toggle-palette nil)
           (dom/focus-palette!))
        (and command? (= code keycode/key-e))
          (do (.preventDefault event) (dispatch! :collection/edit-ns nil) (focus!))
        :else nil))))

(defn on-update [snapshot dispatch!] (dispatch! :collection/write snapshot))

(defn on-remove [e dispatch!] (dispatch! :collection/remove-this nil))

(def style-removed
  {:color (hsl 0 80 100),
   :font-size "14px",
   :font-weight "lighter",
   :background-color (hsl 0 80 40),
   :max-width "400px",
   :padding "0 16px",
   :display "inline-block",
   :margin "32px 16px"})

(defn render [store]
  (fn [state mutate!]
    (let [router (:router store)
          writer (:writer store)
          stack (get-in store [:writer :stack])
          pointer (get-in store [:writer :pointer])
          tree (get-in store (cons :collection (get stack pointer)))]
      (div
       {:style (merge ui/fullscreen ui/row {:background-color (hsl 0 0 0)})}
       (div
        {:style (merge
                 ui/column
                 {:min-width "280px",
                  :color (hsl 0 0 80),
                  :background-color (hsl 0 0 0),
                  :width "20%"})}
        (comp-hot-corner router (:writer store))
        (comp-stack stack pointer))
       (comment comp-debug writer {:background-color (hsl 0 0 0), :z-index 999, :opacity 1})
       (div
        {:style (merge ui/column ui/flex)}
        (if (nil? tree)
          (div {:style style-removed} (comp-text "Tree is be removed." nil))
          (comp-editor
           {:tree tree, :clipboard (:clipboard writer), :focus (:focus writer)}
           on-update
           (on-command store)))
        (div
         {:style (merge ui/row {:background-color (hsl 0 0 0), :justify-content "flex-end"})}
         (div {:style widget/button, :event {:click on-remove}} (comp-text "remove" nil))))))))

(def comp-workspace (create-comp :workspace render))
