
(ns stack-editor.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div span]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.debug :refer [comp-debug]]
            [respo-ui.style :as ui]
            [stack-editor.comp.loading :refer [comp-loading]]
            [stack-editor.comp.analyzer :refer [comp-analyzer]]
            [stack-editor.comp.workspace :refer [comp-workspace]]
            [stack-editor.comp.notifications :refer [comp-notifications]]
            [stack-editor.actions :refer [submit-collection!]]))

(defn on-click [store]
  (fn [e dispatch!] (submit-collection! (:collection store) dispatch!)))

(defn render [store]
  (fn [state mutate!]
    (let [router (:router store)]
      (div
        {:style
         (merge
           ui/global
           {:color (hsl 0 0 70), :background-color (hsl 0 0 0)})}
        (case
          (:name router)
          :loading
          (comp-loading)
          :analyzer
          (comp-analyzer store)
          :workspace
          (comp-workspace store)
          (comp-debug router nil))
        (comp-notifications (:notifications store))
        (comment comp-debug store nil)
        (div
          {:style
           (merge
             ui/button
             {:top "16px", :right "16px", :position "absolute"}),
           :event {:click (on-click store)}}
          (comp-text "Persist" nil))))))

(def comp-container (create-comp :container render))
