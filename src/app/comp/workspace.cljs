
(ns app.comp.workspace
  (:require [hsl.core :refer [hsl]]
            [respo.core :refer [defcomp div <> >> span input]]
            [respo.comp.space :refer [=<]]
            [respo.comp.inspect :refer [comp-inspect]]
            [respo-ui.core :as ui]
            [app.comp.hot-corner :refer [comp-hot-corner]]
            [app.comp.stack :refer [comp-stack]]
            [cirru-editor.comp.editor :refer [comp-editor]]
            [app.util.keycode :as keycode]
            [app.util.dom :as dom]
            [app.util :refer [make-path]]
            [app.style.widget :as widget]))

(defn on-command [store]
  (fn [snapshot dispatch! e]
    (let [code (:key-code e)
          event (:original-event e)
          command? (or (.-metaKey event) (.-ctrlKey event))
          shift? (.-shiftKey event)]
      (cond
        (= code keycode/key-d)
          (do (.preventDefault event) (dispatch! :stack/goto-definition shift?))
        (= code keycode/key-u)
          (do (.preventDefault event) (dispatch! :stack/dependents nil))
        (= code keycode/key-k) (do (.preventDefault event) (dispatch! :stack/go-back nil))
        (= code keycode/key-j) (do (.preventDefault event) (dispatch! :stack/go-next nil))
        (= code keycode/key-s)
          (do (.preventDefault event) (dispatch! :effect/submit shift?))
        (and command? (= code keycode/key-p))
          (do
           (.preventDefault event)
           (.stopPropagation event)
           (dispatch! :router/toggle-palette nil)
           (dom/focus-palette!))
        (and command? (not shift?) (= code keycode/key-e))
          (do (.preventDefault event) (dispatch! :collection/edit-ns nil))
        :else nil))))

(defn on-remove [e dispatch!] (dispatch! :collection/remove-this nil))

(defn on-rename [code-path]
  (fn [e dispatch!]
    (println "the code path:" code-path)
    (dispatch! :modal/mould {:title :rename-path, :data code-path})
    (dom/focus-rename!)))

(defn on-update [snapshot dispatch!] (dispatch! :collection/write snapshot))

(def style-container {:background-color (hsl 0 0 0)})

(def style-debugger {:z-index 999, :background-color (hsl 0 0 0), :opacity 1})

(def style-removed
  {:margin "32px 16px",
   :font-size "20px",
   :font-weight "lighter",
   :color (hsl 0 80 50),
   :font-family "Josefin Sans",
   :padding "0 16px",
   :display "inline-block",
   :max-width "400px"})

(def style-sidebar {:width "180px", :background-color (hsl 0 0 0), :color (hsl 0 0 80)})

(def style-toolbar {:background-color (hsl 0 0 0), :justify-content "flex-start"})

(defcomp
 comp-workspace
 (store)
 (let [router (:router store)
       states (:states store)
       writer (:writer store)
       stack (get-in store [:writer :stack])
       pointer (get-in store [:writer :pointer])
       code-path (get stack pointer)
       tree (if (some? code-path) (get-in store (make-path code-path)) nil)]
   (div
    {:style (merge ui/fullscreen ui/row style-container)}
    (div
     {:style (merge ui/column style-sidebar)}
     (comp-hot-corner router (:writer store))
     (comp-stack stack pointer))
    (comment comp-inspect writer style-debugger)
    (if (some? tree)
      (div
       {:style (merge ui/column ui/flex)}
       (comp-editor
        (>> states :editor)
        {:tree tree, :focus (:focus code-path), :clipboard (:clipboard writer)}
        on-update
        (on-command store))
       (div
        {:style (merge ui/row style-toolbar)}
        (div
         {:inner-text "Rename",
          :class-name "is-unremarkable",
          :style widget/clickable-text,
          :on-click (on-rename code-path)})
        (=< 8 nil)
        (div
         {:inner-text "Delete",
          :class-name "is-unremarkable",
          :style widget/clickable-text,
          :on-click on-remove})))
      (div
       {:style (merge ui/column ui/flex)}
       (div {:style style-removed} (<> span "No expression" nil)))))))
