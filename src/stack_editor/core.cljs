
(ns stack-editor.core
  (:require [respo.core :refer [render! clear-cache!]]
            [stack-editor.schema :as schema]
            [stack-editor.comp.container :refer [comp-container]]
            [cljs.reader :refer [read-string]]
            [stack-editor.updater.core :refer [updater]]
            [stack-editor.util.time :refer [now]]
            [stack-editor.util.keycode :as keycode]
            [stack-editor.util.dom :as dom]
            [stack-editor.actions
             :refer
             [load-collection! submit-collection! submit-changes!]]))

(defonce store-ref (atom schema/store))

(defn dispatch! [op op-data]
  (comment println "dispatch!" op op-data)
  (let [new-store (if (= op :effect/submit)
                    (let [[shift? collection] op-data]
                      (if shift?
                        (submit-collection! collection dispatch!)
                        (submit-changes! collection dispatch!)))
                    (updater @store-ref op op-data (now)))]
    (reset! store-ref new-store)))

(defonce states-ref (atom {}))

(defn render-app! []
  (let [target (.querySelector js/document "#app")]
    (render! (comp-container @store-ref) target dispatch! states-ref)))

(defn on-jsload [] (clear-cache!) (render-app!) (println "code updated."))

(defn -main []
  (enable-console-print!)
  (render-app!)
  (add-watch store-ref :changes render-app!)
  (add-watch states-ref :changes render-app!)
  (.addEventListener
   js/window
   "keydown"
   (fn [event]
     (let [code (.-keyCode event), command? (or (.-metaKey event) (.-ctrlKey event))]
       (cond
         (and command? (= code keycode/key-p))
           (do
            (.preventDefault event)
            (.stopPropagation event)
            (dispatch! :router/toggle-palette nil)
            (dom/focus-palette!))
         :else nil))))
  (println "app started!")
  (load-collection! dispatch!))

(set! js/window.onload -main)
