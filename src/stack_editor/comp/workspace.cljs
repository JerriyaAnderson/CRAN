
(ns stack-editor.comp.workspace
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.debug :refer [comp-debug]]
            [respo-ui.style :as ui]
            [stack-editor.comp.hot-corner :refer [comp-hot-corner]]
            [stack-editor.comp.stack :refer [comp-stack]]
            [cirru-editor.comp.editor :refer [comp-editor]]
            [stack-editor.util.keycode :as keycode]
            [stack-editor.util.dom :as dom]
            [stack-editor.style.widget :as widget]))

(defn update-state [state new-state] (merge state new-state))

(defn on-command [store]
  (fn [snapshot dispatch! e]
    (let [code (:key-code e)
          event (:original-event e)
          command? (or (.-metaKey event) (.-ctrlKey event))
          shift? (.-shiftKey event)]
      (cond
        (= code keycode/key-d)
          (do (.preventDefault event) (dispatch! :stack/goto-definition shift?))
        (= code keycode/key-k) (do (.preventDefault event) (dispatch! :stack/go-back nil))
        (= code keycode/key-j) (do (.preventDefault event) (dispatch! :stack/go-next nil))
        (= code keycode/key-s)
          (do (.preventDefault event) (dispatch! :effect/submit [shift? (:collection store)]))
        (and command? (= code keycode/key-p))
          (do
           (.preventDefault event)
           (.stopPropagation event)
           (dispatch! :router/toggle-palette nil)
           (dom/focus-palette!))
        (and command? (= code keycode/key-e))
          (do (.preventDefault event) (dispatch! :collection/edit-ns nil))
        :else nil))))

(defn on-update [snapshot dispatch!] (dispatch! :collection/write snapshot))

(defn init-state [& args] {:scaled? false})

(defn on-rename [code-path]
  (fn [e dispatch!]
    (println "the code path:" code-path)
    (dispatch! :modal/mould {:title :rename-path, :data code-path})))

(defn on-remove [e dispatch!] (dispatch! :collection/remove-this nil))

(def style-removed
  {:margin "32px 16px",
   :font-size "14px",
   :font-weight "lighter",
   :color (hsl 0 80 100),
   :background-color (hsl 0 80 40),
   :padding "0 16px",
   :display "inline-block",
   :max-width "400px"})

(defn render [store]
  (fn [state mutate!]
    (let [router (:router store)
          writer (:writer store)
          stack (get-in store [:writer :stack])
          pointer (get-in store [:writer :pointer])
          code-path (get stack pointer)
          tree (if (some? code-path) (get-in store (cons :collection code-path)) nil)]
      (div
       {:style (merge ui/fullscreen ui/row {:background-color (hsl 0 0 0)})}
       (div
        {:style (merge
                 ui/column
                 {:width "180px", :background-color (hsl 0 0 0), :color (hsl 0 0 80)})}
        (comp-hot-corner router (:writer store))
        (comp-stack stack pointer))
       (comment comp-debug writer {:z-index 999, :background-color (hsl 0 0 0), :opacity 1})
       (if (some? tree)
         (div
          {:style (merge ui/column ui/flex)}
          (comp-editor
           {:tree tree, :focus (:focus writer), :clipboard (:clipboard writer)}
           on-update
           (on-command store))
          (div
           {:style (merge
                    ui/row
                    {:background-color (hsl 0 0 0), :justify-content "flex-end"})}
           (div
            {:style widget/button,
             :event {:click (on-rename code-path)},
             :attrs {:inner-text "Rename"}})
           (comp-space 8 nil)
           (div
            {:style widget/button, :event {:click on-remove}, :attrs {:inner-text "Remove"}})))
         (div
          {:style (merge ui/column ui/flex)}
          (div {:style style-removed} (comp-text "Tree is be removed." nil))))))))

(def comp-workspace (create-comp :workspace init-state update-state render))
