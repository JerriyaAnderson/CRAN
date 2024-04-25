
(ns stack-editor.comp.analyzer
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div]]
            [respo.comp.space :refer [comp-space]]
            [stack-editor.comp.hot-corner :refer [comp-hot-corner]]
            [stack-editor.comp.navigator :refer [comp-navigator]]
            [stack-editor.comp.definitions :refer [comp-definitions]]
            [stack-editor.comp.namespaces :refer [comp-namespaces]]
            [stack-editor.comp.procedures :refer [comp-procedures]]
            [stack-editor.comp.orphans :refer [comp-orphans]]
            [stack-editor.comp.settings :refer [comp-settings]]))

(defn on-nav [nav-id dispatch!]
  (dispatch! :router/route {:name :analyzer, :data nav-id}))

(defn render [store]
  (fn [state mutate!]
    (let [router (:router store)
          collection (:collection store)
          definitions (:definitions collection)
          namespaces (:namespaces collection)
          procedures (:procedures collection)]
      (div
        {:style
         (merge ui/fullscreen ui/row {:background-color (hsl 0 0 0)})}
        (div
          {:style {:width "20%"}}
          (comp-hot-corner router)
          (comp-space nil "32px")
          (comp-navigator
            [[:definitions "Definitions"]
             [:namespaces "Namespaces"]
             [:procedures "Procedures"]
             [:orphans "Orphans"]
             [:settings "Settings"]]
            on-nav
            (:data router)))
        (case
          (:data router)
          :definitions
          (comp-definitions definitions (keys namespaces))
          :namespaces
          (comp-namespaces namespaces)
          :procedures
          (comp-procedures procedures)
          :orphans
          (comp-orphans)
          :settings
          (comp-settings (:main-definition collection))
          nil)))))

(def comp-analyzer (create-comp :analyzer render))
