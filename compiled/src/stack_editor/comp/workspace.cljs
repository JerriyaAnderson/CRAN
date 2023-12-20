
(ns stack-editor.comp.workspace
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div]]
            [respo-ui.style :as ui]
            [stack-editor.comp.hot-corner :refer [comp-hot-corner]]
            [stack-editor.comp.stack :refer [comp-stack]]))

(defn render [store]
  (fn [state mutate!]
    (let [router (:router store)]
      (div
        {:style (merge ui/fullscreen ui/row)}
        (div
          {:style {:background-color (hsl 0 0 90), :width "20%"}}
          (comp-hot-corner router))))))

(def comp-workspace (create-comp :workspace render))
