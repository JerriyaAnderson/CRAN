
(ns app.comp.graph
  (:require [hsl.core :refer [hsl]]
            [respo.core :refer [defcomp div list-> <> span input button]]
            [respo-ui.core :as ui]
            [app.comp.def :refer [comp-def]]
            [app.util.detect :refer [def-order =def?]]
            [respo.comp.space :refer [=<]]
            [app.style.widget :as widget]
            [clojure.set :as set]))

(defn on-load [e dispatch!] (dispatch! :graph/load-graph nil))

(defn on-orphans [e dispatch!] (dispatch! :graph/show-orphans nil))

(def style-toolbar {:padding 16})

(defn render-toolbar []
  (div
   {:style style-toolbar}
   (div
    {}
    (button {:inner-text "Build tree", :style widget/button, :on-click on-load})
    (=< 8 nil)
    (button {:inner-text "Find orphans", :style widget/button, :on-click on-orphans}))))

(def style-body {:flex 1, :overflow :auto})

(def style-column {:min-width 80, :overflow :auto, :padding "16px 16px", :flex-shrink 0})

(def style-graph {:background-color (hsl 0 0 0), :overflow :auto})

(defcomp
 comp-graph
 (store)
 (div
  {:style (merge ui/expand ui/column style-graph)}
  (render-toolbar)
  (let [tree (get-in store [:graph :tree])
        root-tree (assoc (get-in store [:collection :root]) :deps #{tree})
        view-path (get-in store [:graph :path])]
    (if (some? tree)
      (list->
       {:style (merge ui/row style-body)}
       (loop [branch root-tree, children [], path []]
         (let [next-path (conj path (get view-path (count path)))
               next-pos (get view-path (count path))
               next-children (conj
                              children
                              [(count children)
                               (list->
                                {:style style-column}
                                (->> (:deps branch)
                                     (sort def-order)
                                     (map-indexed
                                      (fn [idx child-node]
                                        [idx
                                         (comp-def
                                          child-node
                                          path
                                          (=def? next-pos child-node))]))))])]
           (if (= path view-path)
             next-children
             (let [next-branch (->> (:deps branch)
                                    (set/select
                                     (fn [x] (=def? (get view-path (count path)) x)))
                                    (first))]
               (recur next-branch next-children next-path))))))
      (<> div "Not generated yet." {:padding "0 16px"})))))
