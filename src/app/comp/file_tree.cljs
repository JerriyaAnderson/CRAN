
(ns app.comp.file-tree
  (:require-macros (respo.macros :refer (defcomp)))
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [div]]
            [respo.comp.text :refer [comp-text]]
            [respo-ui.style :as ui]
            (clojure.string :as string)
            (app.util :refer (segments->tree))))

(def style-file-tree {:background-color (hsl 0 0 0)})

(defn render-toolbar [] (div {} (comp-text "Tools" nil)))

(defcomp
 comp-file-tree
 (store)
 (div
  {:style (merge ui/fullscreen style-file-tree)}
  (render-toolbar)
  (div
   {}
   (let [ns-names (keys (get-in store [:collection :files]))
         segments (->> ns-names (map (fn [x] (string/split x "."))))
         file-tree (segments->tree segments)
         ns-path (get-in store [:graph :ns-path])]
     (loop [children [], path ns-path]
       (let [next-children []]
         (if (empty? path) next-children (recur next-children (rest path)))))))))
