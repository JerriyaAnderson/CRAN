
(ns stack-editor.comp.settings
  (:require [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.text :refer [comp-text]]
            [stack-editor.comp.main-def :refer [comp-main-def]]))

(defn render [main-definition]
  (fn [state mutate!]
    (div
      {:style (merge ui/column ui/flex ui/card)}
      (div {} (comp-main-def (or main-definition "")))
      (comp-space nil "16px"))))

(def comp-settings (create-comp :settings render))
