
(ns app.comp.file-tree
  (:require [hsl.core :refer [hsl]]
            [respo.core :refer [defcomp div list-> >> <> span input button]]
            [respo-ui.core :as ui]
            [clojure.string :as string]
            [app.util :refer [segments->tree]]
            [respo.comp.space :refer [=<]]
            [app.comp.brief-file :refer [comp-brief-file]]
            [app.style.widget :as widget]
            [app.util.keycode :as keycode]))

(defn on-change [cursor] (fn [e dispatch!] (dispatch! :states [cursor (:value e)])))

(defn on-graph [e dispatch!] (dispatch! :router/route {:name :graph, :data nil}))

(defn on-keydown [state cursor]
  (fn [e dispatch!]
    (if (= keycode/key-enter (.-keyCode (:original-event e)))
      (do
       (if (string/includes? state "/")
         (dispatch! :collection/add-definition (string/split state "/"))
         (dispatch! :collection/add-namespace state))
       (dispatch! :states [cursor ""])))))

(defn on-stack [e d! m!] (d! :router/route {:name :workspace, :data nil}))

(defn on-view [path ns-piece]
  (fn [e dispatch!] (dispatch! :graph/view-ns (conj path ns-piece))))

(def style-toolbar {:padding "16px 16px"})

(defn render-toolbar [state cursor]
  (div
   {:style style-toolbar}
   (button {:inner-text "Graph", :style widget/button, :on-click on-graph})
   (=< 8 nil)
   (button {:inner-text "Stack", :style widget/button, :on-click on-stack})
   (=< 8 nil)
   (input
    {:value state,
     :placeholder "ns/def or ns",
     :style widget/input,
     :on-input (on-change cursor),
     :on-keydown (on-keydown state cursor)})))

(def style-body {:flex 1, :overflow :auto})

(def style-column {:padding "16px 16px", :min-width 80, :line-height 1.6, :overflow :auto})

(def style-file
  {:cursor :pointer, :color (hsl 0 0 100 0.5), :white-space :nowrap, :font-size 16})

(def style-file-tree {:background-color (hsl 0 0 0)})

(def style-highlight {:color (hsl 0 0 100 0.9)})

(defcomp
 comp-file-tree
 (states store)
 (let [cursor (:cursor states)
       state (:data states)
       ns-path (get-in store [:graph :ns-path])
       ns-text (string/join "." ns-path)
       files (get-in store [:collection :files])]
   (div
    {:style (merge ui/fullscreen ui/column style-file-tree)}
    (render-toolbar state cursor)
    (div
     {:style (merge ui/row style-body)}
     (list->
      {:style ui/row}
      (let [ns-names (keys files)
            segments (->> ns-names (map (fn [x] (string/split x "."))))
            file-tree (segments->tree segments)]
        (loop [children [], path []]
          (let [next-piece (get ns-path (count path))
                dict (get-in file-tree path)
                next-children (conj
                               children
                               [(string/join "/" path)
                                (if (map? dict)
                                  (list->
                                   {:style style-column}
                                   (->> dict
                                        (sort compare)
                                        (map
                                         (fn [entry]
                                           (let [ns-piece (key entry)]
                                             [ns-piece
                                              (div
                                               {:style (merge
                                                        style-file
                                                        (if (= ns-piece next-piece)
                                                          style-highlight)),
                                                :on-click (on-view path ns-piece)}
                                               (<> span ns-piece nil)
                                               (=< 8 nil)
                                               (let [info (get dict ns-piece)]
                                                 (cond
                                                   (map? info) (<> span (count info) nil)
                                                   (= :file info) (<> span "." nil)
                                                   :else nil)))])))))
                                  (span {}))])]
            (if (= path ns-path) next-children (recur next-children (conj path next-piece)))))))
     (=< 64 nil)
     (if (contains? files ns-text)
       (comp-brief-file (>> states ns-text) ns-text (get files ns-text)))))))
