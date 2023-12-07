
(ns stack-editor.comp.namespaces
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer (create-comp div)]))

(defn render [definitions] (fn [state mutate!] (div {})))

(def comp-namespaces (create-comp :namespaces render))
