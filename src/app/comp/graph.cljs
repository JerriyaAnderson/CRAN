
(ns app.comp.graph
  (:require [hsl.core :refer [hsl]] [respo.alias :refer [create-comp div]]))

(def comp-graph (create-comp :graph (fn [store] (fn [cursor] (div {})))))
