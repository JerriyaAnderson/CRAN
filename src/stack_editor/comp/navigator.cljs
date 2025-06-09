
(ns stack-editor.comp.navigator
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div]]
            [respo.comp.text :refer [comp-text]]))

(defn handle-nav [entry-id on-nav] (fn [e dispatch!] (on-nav entry-id dispatch!)))

(def style-entry
  {:line-height "40px",
   :color (hsl 0 0 50),
   :font-size "15px",
   :font-weight "lighter",
   :cursor "pointer",
   :padding "0 16px"})

(defn render [entries on-nav router-data]
  (fn [state mutate!]
    (div
     {}
     (->> entries
          (map-indexed
           (fn [idx entry]
             [idx
              (div
               {:style (merge
                        style-entry
                        (if (= router-data (first entry)) {:color (hsl 0 0 80)})),
                :event {:click (handle-nav (first entry) on-nav)}}
               (comp-text (last entry) nil))]))))))

(def comp-navigator (create-comp :navigator render))
