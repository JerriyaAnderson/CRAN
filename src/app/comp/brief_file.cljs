
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

(defn on-input [e d! m!] (m! (:value e)))

(defn on-keydown [ns-text def-text]
  (fn [e d! m!]
    (println "event")
    (if (= keycode/key-enter (:key-code e))
      (if (not (string/blank? def-text))
        (do (d! :collection/add-definition [ns-text def-text]) (m! ""))))))

(defn on-remove [ns-text] (fn [e d! m!] (d! :collection/remove-file ns-text)))

(def style-file {:padding "16px", :font-size 16, :line-height 1.6})

(def style-link {:cursor :pointer})

(defcomp
 comp-brief-file
 (states ns-text file)
 (let [state (or (:data states) "")]
   (div
    {:style style-file}
    (div
     {:style ui/row}
     (<> span ns-text nil)
     (=< 16 nil)
     (span {:inner-text "ns", :style style-link, :on-click (on-edit-ns ns-text)})
     (=< 16 nil)
     (span {:inner-text "procs", :style style-link, :on-click (on-edit-procs ns-text)})
     (=< 16 nil)
     (span
      {:inner-text "Delete", :style widget/clickable-text, :on-click (on-remove ns-text)}))
    (div
     {}
     (input
      {:value state,
       :placeholder "new def",
       :style widget/input,
       :on {:input on-input, :keydown (on-keydown ns-text state)}}))
    (list->
     {}
     (->> (:defs file)
          (sort compare)
          (map
           (fn [entry]
             (let [def-text (key entry)]
               [def-text
                (div
                 {:inner-text (str def-text "↗"),
                  :style style-link,
                  :on-click (on-edit-def ns-text def-text)})]))))))))
