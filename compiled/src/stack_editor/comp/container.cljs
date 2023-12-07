
(ns stack-editor.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div span]]
            [respo.comp.debug :refer [comp-debug]]
            [respo-ui.style :as ui]
            [stack-editor.comp.loading :refer [comp-loading]]
            [stack-editor.comp.analyzer :refer [comp-analyzer]]
            [stack-editor.comp.workspace :refer [comp-workspace]]))

(defn render [store]
  (fn [state mutate!]
    (let [router (:router store)]
      (div
        {:style (merge ui/global)}
        (case
          (:name router)
          :loading
          (comp-loading)
          :analyzer
          (comp-analyzer store)
          :workspace
          (comp-workspace store)
          (comp-debug router nil))
        (comment comp-debug store nil)))))

(def comp-container (create-comp :container render))
