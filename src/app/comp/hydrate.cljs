
(ns app.comp.hydrate
  (:require-macros [respo.macros :refer [defcomp <> div textarea button span]])
  (:require [hsl.core :refer [hsl]]
            [respo.core :refer [create-comp]]
            [respo.comp.space :refer [=<]]
            [respo-ui.style :as ui]
            [app.style.widget :as widget]
            [cljs.reader :refer [read-string]]
            [app.util.detect :refer [cirru-vec?]]))

(def style-textarea
  {:background-color (hsl 0 0 100 0.2),
   :font-family "Source Code Pro, Menlo",
   :color :white,
   :resize :none,
   :width 640,
   :height 200,
   :line-height "24px"})

(def style-hint {:font-family "Hind"})

(def style-toolbar {:justify-content :flex-end})

(defn on-change [cursor] (fn [e dispatch!] (dispatch! :states [cursor (:value e)])))

(defn on-hydrate [text]
  (fn [e dispatch!]
    (let [piece (read-string text)]
      (if (cirru-vec? piece)
        (do (dispatch! :collection/hydrate piece) (dispatch! :modal/recycle nil))
        (dispatch! :notification/add-one (str "Checking failed: " (pr-str text)))))))

(defcomp
 comp-hydrate
 (states)
 (let [state (:data states)]
   (div
    {}
    (div {:style style-hint} (<> span "EDN Cirru code to hydrate:" nil))
    (div
     {}
     (textarea
      {:style (merge ui/textarea style-textarea),
       :attrs {:value state},
       :event {:input (on-change cursor)}}))
    (=< nil 8)
    (div
     {:style (merge ui/row style-toolbar)}
     (button
      {:style widget/button, :event {:click (on-hydrate state)}}
      (<> span "Hydrate" nil))))))
