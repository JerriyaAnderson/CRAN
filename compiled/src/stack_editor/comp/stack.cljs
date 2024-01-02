
(ns stack-editor.comp.stack
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]))

(defn render [stack pointer]
  (fn [state mutate!]
    (div
      {}
      (->>
        stack
        (map-indexed
          (fn [idx item] [idx
                          (div
                            {:style
                             {:line-height 3,
                              :cursor "pointer",
                              :padding "0 16px"}}
                            (comp-text item nil))]))))))

(def comp-stack (create-comp :stack render))
