
(ns app.comp.file-tree
  (:require-macros [respo.macros :refer [defcomp <> div button input span]])
  (:require [hsl.core :refer [hsl]]
            [respo.core :refer [create-comp]]
            [respo-ui.style :as ui]
            [clojure.string :as string]
            [app.util :refer [segments->tree]]
            [respo.comp.space :refer [=<]]
            [app.comp.brief-file :refer [comp-brief-file]]
            [app.style.widget :as widget]
            [app.util.keycode :as keycode]))

(def style-body {:flex 1, :overflow :auto})

(def style-highlight {:color (hsl 0 0 100 0.9)})

(def style-toolbar {:padding "16px 16px"})

(def style-column {:padding "16px 16px", :min-width 80, :line-height 1.6, :overflow :auto})

(defn on-view [path ns-piece]
  (fn [e dispatch!] (dispatch! :graph/view-ns (conj path ns-piece))))

(defn on-change [cursor] (fn [e dispatch!] (dispatch! :states [cursor (:value e)])))

(def style-file-tree {:background-color (hsl 0 0 0)})

(defn on-keydown [state cursor]
  (fn [e dispatch!]
    (if (= keycode/key-enter (.-keyCode (:original-event e)))
      (do
       (if (string/includes? state "/")
         (dispatch! :collection/add-definition (string/split state "/"))
         (dispatch! :collection/add-namespace state))
       (dispatch! :states [cursor ""])))))

(defn on-graph [e dispatch!] (dispatch! :router/route {:name :graph, :data nil}))

(defn render-toolbar [state cursor]
  (div
   {:style style-toolbar}
   (button {:inner-text "Graph", :style widget/button, :event {:click on-graph}})
   (=< 8 nil)
   (input
    {:value state,
     :placeholder "ns/def or ns",
     :style widget/input,
     :event {:input (on-change cursor), :keydown (on-keydown state cursor)}})))

(def style-file
  {:cursor :pointer, :color (hsl 0 0 100 0.5), :white-space :nowrap, :font-size 16})

(defcomp
 comp-file-tree
 (states store)
 (let [state (:data states)
       ns-path (get-in store [:graph :ns-path])
       ns-text (string/join "." ns-path)
       files (get-in store [:collection :files])]
   (div
    {:style (merge ui/fullscreen ui/column style-file-tree)}
    (render-toolbar state cursor)
    (div
     {:style (merge ui/row style-body)}
     (div
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
                                  (div
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
                                                :event {:click (on-view path ns-piece)}}
                                               (<> span ns-piece nil)
                                               (=< 8 nil)
                                               (let [info (get dict ns-piece)]
                                                 (cond
                                                   (map? info) (<> span (count info) nil)
                                                   (= :file info) (<> span "." nil)
                                                   :else nil)))])))))
                                  nil)])]
            (if (= path ns-path) next-children (recur next-children (conj path next-piece)))))))
     (=< 64 nil)
     (if (contains? files ns-text) (comp-brief-file ns-text (get files ns-text)))))))
