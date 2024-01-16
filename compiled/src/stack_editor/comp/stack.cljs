
(ns stack-editor.comp.stack
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]))

(defn on-click [pointer]
  (fn [e dispatch!] (dispatch! :stack/point-to pointer)))

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
                             (merge
                               {:line-height 3,
                                :color (hsl 0 0 60),
                                :cursor "pointer",
                                :padding "0 16px"}
                               (if
                                 (= idx pointer)
                                 {:color (hsl 0 0 90)})),
                             :event {:click (on-click idx)}}
                            (comp-text item nil))]))))))

(def comp-stack (create-comp :stack render))
