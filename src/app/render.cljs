
(ns app.render
  (:require [respo.alias :refer [html head title script style meta' div link body]]
            [respo.render.html :refer [make-html make-string]]
            [app.comp.container :refer [comp-container]]
            ["fs" :refer [readFileSync writeFileSync]]
            [app.schema :as schema]))

(defn spit [file-name content]
  (writeFileSync file-name content)
  (println "Wrote to:" file-name))

(def icon-url "http://logo.cirru.org/cirru-400x400.png")

(defn html-dsl [resources html-content]
  (make-html
   (html
    {}
    (head
     {}
     (title {:innerHTML "Stack Editor"})
     (link {:rel "icon", :type "image/png", :href icon-url})
     (link {:rel "manifest", :href "manifest.json"})
     (meta' {:charset "utf8"})
     (meta' {:name "viewport", :content "width=device-width, initial-scale=1"})
     (if (:build? resources) (meta' {:id "server-rendered", :type "text/edn"}))
     (if (contains? resources :css)
       (link {:rel "stylesheet", :type "text/css", :href (:css resources)})))
    (body
     {}
     (div {:id "app", :innerHTML html-content})
     (if (:build? resources) (script {:src (:vendor resources)}))
     (script {:src (:main resources)})))))

(defn generate-empty-html [] (html-dsl {:build? false, :main "/main.js"} ""))

(defn slurp [x] (readFileSync x "utf8"))

(defn generate-html []
  (let [tree (comp-container schema/store)
        html-content (make-string tree)
        resources (let [manifest (js/JSON.parse (slurp "dist/manifest.json"))]
                    {:build? true,
                     :css (aget manifest "main.css"),
                     :main (aget manifest "main.js"),
                     :vendor (aget manifest "vendor.js")})]
    (html-dsl resources html-content)))

(defn main! []
  (spit
   "dist/index.html"
   (if (= js/process.env.env "dev") (generate-empty-html) (generate-html))))

(main!)
