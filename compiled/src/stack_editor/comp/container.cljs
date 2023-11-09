
(ns stack-editor.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div span]]))

(defn render [store]
  (fn [state mutate!]
    (div {} (span {:attrs {:inner-text "Container"}}))))

(def comp-container (create-comp :container render))
