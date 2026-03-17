
(ns stack-editor.main
  (:require [respo.core
             :refer
             [render! clear-cache! render-element falsify-stage! gc-states!]]
            [stack-editor.schema :as schema]
            [stack-editor.comp.container :refer [comp-container]]
            [cljs.reader :refer [read-string]]
            [stack-editor.updater.core :refer [updater]]
            [stack-editor.util.keycode :as keycode]
            [stack-editor.util.dom :as dom]
            [stack-editor.util :refer [now!]]
            [stack-editor.actions
             :refer
             [load-collection! submit-collection! submit-changes! display-code!]]
            [cirru-editor.util.dom :refer [focus!]]))

(defonce store-ref (atom schema/store))

(def focus-moved?-ref (atom false))

(defn dispatch! [op op-data]
  (println "dispatch!" op op-data)
  (case op
    :effect/submit
      (let [shift? op-data, sepal-data (:collection @store-ref)]
        (if shift?
          (submit-collection! sepal-data dispatch!)
          (submit-changes! sepal-data dispatch!)))
    :effect/dehydrate (display-code! @store-ref)
    :effect/load (load-collection! dispatch! false)
    (let [new-store (updater @store-ref op op-data (now!))]
      (reset!
       focus-moved?-ref
       (not
        (and (identical? (:collection @store-ref) (:collection new-store))
             (identical? (:writer @store-ref) (:writer new-store)))))
      (reset! store-ref new-store))))

(defonce states-ref (atom {}))

(defn render-app! []
  (let [target (.querySelector js/document "#app")]
    (render! (comp-container @store-ref #{:shell :dynamic}) target dispatch! states-ref)
    (if @focus-moved?-ref (do (reset! focus-moved?-ref false) (focus!)))))

(def ssr-stages
  (let [ssr-element (.querySelector js/document "#ssr-stages")
        ssr-markup (.getAttribute ssr-element "content")]
    (read-string ssr-markup)))

(defn -main! []
  (enable-console-print!)
  (if (not (empty? ssr-stages))
    (let [target (.querySelector js/document "#app")]
      (falsify-stage!
       target
       (render-element (comp-container @store-ref ssr-stages) states-ref)
       dispatch!)))
  (render-app!)
  (add-watch store-ref :gc (fn [] (gc-states! states-ref)))
  (add-watch store-ref :changes render-app!)
  (add-watch states-ref :changes render-app!)
  (.addEventListener
   js/window
   "keydown"
   (fn [event]
     (let [code (.-keyCode event)
           command? (or (.-metaKey event) (.-ctrlKey event))
           shift? (.-shiftKey event)]
       (cond
         (and command? (= code keycode/key-p))
           (do
            (.preventDefault event)
            (.stopPropagation event)
            (dispatch! :router/toggle-palette nil)
            (dom/focus-palette!))
         (and shift? command? (= code keycode/key-a))
           (do
            (let [router (:router @store-ref), writer (:writer @store-ref)]
              (if (= (:name router) :workspace)
                (dispatch! :router/route {:name :analyzer, :data :definitions})
                (if (not (empty? (:stack writer)))
                  (dispatch! :router/route {:name :workspace, :data nil})))))
         :else nil))))
  (println "app started!")
  (load-collection! dispatch! true))

(defn on-jsload! [] (clear-cache!) (render-app!) (println "Code updated."))

(set! js/window.onload -main!)
