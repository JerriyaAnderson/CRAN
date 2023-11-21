
(ns stack-editor.comp.editor-container
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div]]))

(defn render [store] (fn [state mutate!] (div {})))

(def comp-editor-container (create-comp :editor-container render))
