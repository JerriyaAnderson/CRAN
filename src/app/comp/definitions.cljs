
(ns app.comp.definitions
  (:require [hsl.core :refer [hsl]]
            [clojure.string :as string]
            [respo.alias :refer [create-comp div input]]
            [respo.cursor :refer [with-cursor]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.debug :refer [comp-debug]]
            [respo-ui.style :as ui]
            [respo-border.transform.space :refer [interpose-spaces]]
            [app.style.widget :as widget]
            [app.comp.ns-creator :refer [comp-ns-creator]]
            [app.comp.file :refer [comp-file]]))

(defn by-ns-part [entry]
  (let [path (first entry), ns-name (first (string/split path (re-pattern "/")))] ns-name))

(def comp-definitions
  (create-comp
   :definitions
   (fn [states sepal-data]
     (fn [cursor]
       (let [files (:files sepal-data)]
         (div
          {:style (merge ui/fullscreen ui/column ui/card {:background-color :black})}
          (comp-space nil "16px")
          (with-cursor
           :ns-creator
           (comp-ns-creator (:ns-creator states) (:package sepal-data)))
          (comp-space nil "32px")
          (div
           {:style (merge ui/flex {:overflow "auto", :padding-bottom 200})}
           (->> files
                (sort-by first)
                (map
                 (fn [entry]
                   (let [ns-name (first entry), def-codes (:defs (val entry))]
                     [ns-name
                      (with-cursor
                       ns-name
                       (comp-file (get states ns-name) def-codes ns-name))])))))))))))
