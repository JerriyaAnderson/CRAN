
(ns stack-editor.comp.hydrate
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div textarea button]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.debug :refer [comp-debug]]
            [respo-ui.style :as ui]
            [stack-editor.style.widget :as widget]
            [cljs.reader :refer [read-string]]
            [stack-editor.util.detect :refer [cirru-vec?]]))

(defn update-state [state text] text)

(def style-textarea
  {:line-height "24px",
   :color :white,
   :background-color (hsl 0 0 100 0.2),
   :width 640,
   :resize :none,
   :font-family "Source Code Pro, Menlo",
   :height 200})

(def style-hint {:font-family "Hind"})

(def style-toolbar {:justify-content :flex-end})

(defn on-change [mutate!] (fn [e dispatch!] (mutate! (:value e))))

(defn init-state [& args] "")

(defn on-hydrate [text]
  (fn [e dispatch!]
    (let [piece (read-string text)]
      (if (cirru-vec? piece)
        (do (dispatch! :collection/hydrate piece) (dispatch! :modal/recycle nil))
        (dispatch! :notification/add-one (str "Checking failed: " (pr-str text)))))))

(defn render []
  (fn [state mutate!]
    (div
     {}
     (div {:style style-hint} (comp-text "EDN Cirru code to hydrate:" nil))
     (div
      {}
      (textarea
       {:style (merge ui/textarea style-textarea),
        :event {:input (on-change mutate!)},
        :attrs {:value state}}))
     (comp-space nil 8)
     (div
      {:style (merge ui/row style-toolbar)}
      (button
       {:style widget/button, :event {:click (on-hydrate state)}}
       (comp-text "Hydrate" nil))))))

(def comp-hydrate (create-comp :hydrate init-state update-state render))
