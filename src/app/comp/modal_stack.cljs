
(ns app.comp.modal-stack
  (:require [hsl.core :refer [hsl]]
            [respo.core :refer [defcomp list-> div <> >> span input]]
            [respo-ui.core :as ui]
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
     :rename-path (comp-rename-path (>> states :rename-path) data)
     :hydrate (comp-hydrate (>> states :hydrate))
     :orphans (comp-orphans data)
     (<> span title nil))))

(defcomp
 comp-modal-stack
 (states modal-stack)
 (list->
  {}
  (->> modal-stack
       (map-indexed
        (fn [idx modal]
          (let [kind (:kind modal), title (:title modal), data (:data modal)]
            [idx
             (div
              {:style style-modal, :on-click on-recycle}
              (div {:on-click on-tip} (renderer states kind title data)))]))))))
