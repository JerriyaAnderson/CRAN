
(ns stack-editor.comp.procedure
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div]]))

(defn render [store] (fn [state mutate!] (div {})))

(def comp-procedure (create-comp :procedure render))
