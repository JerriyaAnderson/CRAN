
(ns app.comp.hot-corner
  (:require-macros (respo.macros :refer (defcomp)))
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [div span]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [respo-ui.style :as ui]))

(defn on-switch [router writer]
  (fn [e dispatch!]
    (if (= (:name router) :workspace)
      (dispatch! :router/route {:name :graph, :data nil})
      (if (not (empty? (:stack writer)))
        (dispatch! :router/route {:name :workspace, :data nil})))))

(defcomp
 comp-hot-corner
 (router writer)
 (div
  {:style {:font-size "24px", :font-weight "300", :text-align "center", :cursor "pointer"},
   :event {:click (on-switch router writer)}}
  (comp-text "Stack Editor" {:font-family "Josefin Sans"})))
