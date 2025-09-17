
(ns stack-editor.comp.analyzer
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div]]
            [respo.comp.space :refer [comp-space]]
            [stack-editor.comp.definitions :refer [comp-definitions]]))

(defn on-nav [nav-id dispatch!] (dispatch! :router/route {:name :analyzer, :data nav-id}))

(defn render [store]
  (fn [state mutate!]
    (let [router (:router store)
          collection (:collection store)
          definitions (:definitions collection)
          namespaces (:namespaces collection)
          procedures (:procedures collection)]
      (div
       {:style (merge ui/fullscreen ui/row {:background-color (hsl 0 0 0)})}
       (comp-definitions definitions (keys namespaces))))))

(def comp-analyzer (create-comp :analyzer render))
