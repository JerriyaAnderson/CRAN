
(ns stack-editor.comp.hot-corner
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div]]))

(defn render [store] (fn [state mutate!] (div {})))

(def comp-hot-corner (create-comp :hot-corner render))
