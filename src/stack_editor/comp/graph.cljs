
(ns stack-editor.comp.graph
  (:require [hsl.core :refer [hsl]] [respo.alias :refer [create-comp div]]))

(defn render [store] (fn [cursor] (div {})))

(def comp-graph (create-comp :graph render))
