
(ns app.page
  (:require [respo.core :refer [create-element]]
            [respo.core :refer [html head title script style meta' div link body]]
            [respo.render.html :refer [make-string]]
            [app.comp.container :refer [comp-container]]
            [shell-page.core :refer [make-page slurp spit]]
            [app.schema :as schema]
            [cljs.reader :refer [read-string]]))

(def base-info
  {:title "Stack Editor",
   :icon "http://logo.cirru.org/cirru-400x400.png",
   :ssr nil,
   :inilne-html "",
   :styles ["http://cdn.tiye.me/favored-fonts/main-fonts.css"]})

(defn prod-page []
  (let [html-content (make-string (comp-container schema/store))
        assets (read-string (slurp "dist/assets.edn"))]
    (make-page
     html-content
     (merge base-info {:styles [], :scripts (map (fn [x] (:output-name x)) assets)}))))

(defn dev-page [] (make-page "" (merge base-info {:scripts ["/client.js"]})))

(defn main! []
  (if (= js/process.env.env "dev")
    (spit "target/index.html" (dev-page))
    (spit "dist/index.html" (prod-page))))
