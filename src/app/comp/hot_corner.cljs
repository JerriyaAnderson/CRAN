
(ns app.comp.hot-corner
  (:require-macros [respo.macros :refer [defcomp <> div span]])
  (:require [hsl.core :refer [hsl]]
            [respo.core :refer [create-comp]]
            [respo.comp.space :refer [=<]]
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
  (<> span "Stack Editor" {:font-family "Josefin Sans"})))
