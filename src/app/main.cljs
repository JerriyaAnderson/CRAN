
(ns app.main
  (:require [respo.core :refer [render! clear-cache! render-element realize-ssr!]]
            [app.schema :as schema]
            [app.comp.container :refer [comp-container]]
            [cljs.reader :refer [read-string]]
            [app.updater :refer [updater]]
            [app.util.keycode :as keycode]
            [app.util.dom :as dom]
            [app.util :refer [now!]]
            [app.actions
             :refer
             [load-collection! submit-collection! submit-changes! display-code!]]
            [cirru-editor.util.dom :refer [focus!]]))

(def *focus-moved? (atom false))

(defonce *store (atom schema/store))

(defn dispatch! [op op-data]
  (println "Dispatch!" op)
  (case op
    :effect/submit
      (let [shift? op-data, sepal-data (:collection @*store)]
        (if shift?
          (submit-collection! sepal-data dispatch!)
          (submit-changes! sepal-data dispatch!)))
    :effect/dehydrate (display-code! @*store)
    :effect/load (load-collection! dispatch! false)
    (let [new-store (updater @*store op op-data (now!))]
      (reset!
       *focus-moved?
       (not
        (and (identical? (:collection @*store) (:collection new-store))
             (identical? (:writer @*store) (:writer new-store)))))
      (reset! *store new-store))))

(def mount-target (.querySelector js/document ".app"))

(defn render-app! [renderer]
  (renderer mount-target (comp-container @*store) dispatch!)
  (if @*focus-moved? (do (reset! *focus-moved? false) (focus!))))

(def ssr? (some? (.querySelector js/document "meta.server-rendered")))

(defn main! []
  (if ssr? (render-app! realize-ssr!))
  (render-app! render!)
  (add-watch *store :changes (fn [] (render-app! render!)))
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
            (let [router (:router @*store), writer (:writer @*store)]
              (if (= (:name router) :workspace)
                (dispatch! :router/route {:name :graph, :data nil})
                (if (not (empty? (:stack writer)))
                  (dispatch! :router/route {:name :workspace, :data nil})))))
         :else nil))))
  (println "app started!")
  (load-collection! dispatch! true))

(defn reload! [] (clear-cache!) (render-app! render!) (println "Code updated."))
