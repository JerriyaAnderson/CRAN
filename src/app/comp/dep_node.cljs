
(ns app.comp.dep-node
  (:require-macros (respo.macros :refer (defcomp)))
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [div]]
            [respo.comp.text :refer [comp-text]]
            [respo-ui.style :as ui]
            (clojure.set :refer (difference))))

(def style-name
  {:padding "0 4px", :white-space :nowrap, :line-height "20px", :cursor :pointer})

(def style-external
  {:color (hsl 0 0 70 0.7), :font-size 12, :line-height 1, :cursor :default})

(defn render-external-name [dep-info]
  (div
   {:inner-text (str (:ns dep-info) "\n" "/" (:def dep-info)),
    :style (merge style-name style-external ui/column)}))

(defn on-edit [dep-info]
  (fn [e dispatch!]
    (dispatch!
     :collection/edit
     {:ns (:ns dep-info), :kind :defs, :extra (:def dep-info), :focus []})))

(defn render-internal-name [dep-info]
  (div
   {:inner-text (str (:ns dep-info) "/" (:def dep-info)),
    :style (merge style-name),
    :event {:click (on-edit dep-info)}}))

(def style-dependency
  {:border-left (str "1px solid " (hsl 0 0 100 0.3)),
   :padding-left 8,
   :align-items :flex-start})

(defcomp
 comp-dep-node
 (dep-info)
 (let [external? (:external? dep-info)]
   (div
    {:style (merge ui/row style-dependency)}
    (if external? (render-external-name dep-info) (render-internal-name dep-info))
    (let [deps (:deps dep-info)
          external-deps (into #{} (filter :external? deps))
          internal-deps (difference deps external-deps)]
      (if (not (empty? deps))
        (div
         {}
         (if (not (empty? external-deps))
           (div
            {:style ui/row}
            (->> external-deps
                 (map
                  (fn [child-dep-info]
                    (let [{ns-text :ns, def-text :def} child-dep-info]
                      [(str ns-text "/" def-text) (comp-dep-node child-dep-info)]))))))
         (if (not (empty? internal-deps))
           (div
            {}
            (->> internal-deps
                 (map
                  (fn [child-dep-info]
                    (let [{ns-text :ns, def-text :def} child-dep-info]
                      [(str ns-text "/" def-text) (comp-dep-node child-dep-info)]))))))))))))
