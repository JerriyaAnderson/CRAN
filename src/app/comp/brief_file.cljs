
(ns app.comp.brief-file
  (:require [hsl.core :refer [hsl]]
            [respo.core :refer [defcomp div list-> <> span input]]
            [clojure.string :as string]
            [respo-ui.core :as ui]
            [respo.comp.space :refer [=<]]
            [app.style.widget :as widget]
            [app.util.keycode :as keycode]))

(defn on-edit-def [ns-text def-text]
  (fn [e dispatch!]
    (dispatch! :collection/edit {:kind :defs, :ns ns-text, :extra def-text, :focus [2]})))

(defn on-edit-ns [ns-text]
  (fn [e dispatch!]
    (dispatch! :collection/edit {:kind :ns, :ns ns-text, :extra nil, :focus []})))

(defn on-edit-procs [ns-text]
  (fn [e dispatch!]
    (dispatch! :collection/edit {:kind :procs, :ns ns-text, :extra nil, :focus []})))

(defn on-remove [ns-text] (fn [e d! m!] (d! :collection/remove-file ns-text)))

(def style-file {:padding "0 8px 16px 8px", :line-height 1.6})

(defcomp
 comp-brief-file
 (states ns-text file)
 (let [cursor (:cursor states), state (or (:data states) "")]
   (div
    {:style style-file}
    (div
     {:style ui/row}
     (<> ns-text nil)
     (=< 16 nil)
     (span {:inner-text "ns", :style {:cursor :pointer}, :on-click (on-edit-ns ns-text)})
     (=< 16 nil)
     (span
      {:inner-text "procs", :style {:cursor :pointer}, :on-click (on-edit-procs ns-text)})
     (=< 16 nil)
     (span
      {:inner-text "Delete", :style widget/clickable-text, :on-click (on-remove ns-text)}))
    (div
     {}
     (input
      {:value state,
       :placeholder "new def",
       :style widget/input,
       :on-input (fn [e d!] (d! cursor (:value e))),
       :on-keydown (fn [e d!]
         (if (= keycode/key-enter (:key-code e))
           (if (not (string/blank? state))
             (do (d! :collection/add-definition [ns-text state]) (d! cursor "")))))}))
    (=< nil 8)
    (list->
     {}
     (->> (:defs file)
          (sort compare)
          (map
           (fn [entry]
             (let [def-text (key entry)]
               [def-text
                (div
                 {:inner-text def-text,
                  :style {:cursor :pointer,
                          :font-size 14,
                          :line-height "24px",
                          :color (hsl 0 0 50)},
                  :on-click (on-edit-def ns-text def-text)})]))))))))

(defn on-keydown [ns-text def-text]
  (fn [e d! m!]
    (println "event")
    (if (= keycode/key-enter (:key-code e))
      (if (not (string/blank? def-text))
        (do (d! :collection/add-definition [ns-text def-text]) (m! ""))))))
