
(ns app.comp.modal-stack
  (:require-macros [respo.macros :refer [defcomp cursor-> <> div span]])
  (:require [hsl.core :refer [hsl]]
            [respo.core :refer [create-comp]]
            [respo-ui.style :as ui]
            [app.style.widget :as widget]
            [app.comp.rename-path :refer [comp-rename-path]]
            [app.comp.hydrate :refer [comp-hydrate]]
            [app.comp.orphans :refer [comp-orphans]]))

(defn on-tip [e dispatch!] )

(def style-modal
  (merge
   ui/center
   {:background-color (hsl 0 0 0 0.6),
    :z-index 900,
    :position :fixed,
    :top 0,
    :right 0,
    :width "100%",
    :height "100%"}))

(defn on-recycle [e dispatch!] (dispatch! :modal/recycle nil))

(defn renderer [states kind title data]
  (div
   {}
   (case title
     :rename-path (cursor-> :rename-path comp-rename-path states data)
     :hydrate (cursor-> :hydrate comp-hydrate states)
     :orphans (comp-orphans data)
     (<> span title nil))))

(defcomp
 comp-modal-stack
 (states modal-stack)
 (div
  {}
  (->> modal-stack
       (map-indexed
        (fn [idx modal]
          (let [kind (:kind modal), title (:title modal), data (:data modal)]
            [idx
             (div
              {:style style-modal, :event {:click on-recycle}}
              (div {:event {:click on-tip}} (renderer states kind title data)))]))))))
