
(ns stack-editor.comp.modal-stack
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div span]]
            [respo.cursor :refer [with-cursor]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.debug :refer [comp-debug]]
            [respo-ui.style :as ui]
            [stack-editor.style.widget :as widget]
            [stack-editor.comp.rename-path :refer [comp-rename-path]]
            [stack-editor.comp.hydrate :refer [comp-hydrate]]))

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
     :rename-path (with-cursor :rename-path (comp-rename-path (:rename-path states) data))
     :hydrate (with-cursor :hydrate (comp-hydrate (:hydrate states)))
     (comp-text title nil))))

(def comp-modal-stack
  (create-comp
   :modal-stack
   (fn [states modal-stack]
     (fn [cursor]
       (div
        {}
        (->> modal-stack
             (map-indexed
              (fn [idx modal]
                (let [kind (:kind modal), title (:title modal), data (:data modal)]
                  [idx
                   (div
                    {:style style-modal, :event {:click on-recycle}}
                    (div {:event {:click on-tip}} (renderer states kind title data)))])))))))))
