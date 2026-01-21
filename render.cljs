
(ns stack-editor.boot
  (:require
    [respo.alias :refer [html head title script style meta' div link body]]
    [respo.render.html :refer [make-html make-string]]
    [stack-editor.comp.container :refer [comp-container]]
    [stack-editor.schema :as schema]))

(defn html-dsl [data html-content ssr-stages]
  (make-html
    (html {}
      (head {}
        (title {:attrs {:innerHTML "Stack Editor"}})
        (link {:attrs {:rel "icon" :type "image/png" :href "http://logo.cirru.org/cirru-400x400.png"}})
        (link {:attrs {:rel "stylesheet" :href "style.css"}})
        (meta' {:attrs {:charset "utf-8"}})
        (meta' {:attrs {:name "viewport" :content "width=device-width, initial-scale=1"}})
        (meta' {:attrs {:id "ssr-stages" :content (pr-str ssr-stages)}})
        (style {:attrs {:innerHTML "body {margin: 0;}"}} )
        (style {:attrs {:innerHTML "body * {box-sizing: border-box;}"}})
        (script {:attrs {:id "config" :type "text/edn" :innerHTML (pr-str data)}}))
      (body {}
        (div {:attrs {:id "app" :innerHTML html-content}})
        (script {:attrs {:src "main.js"}})))))

(defn spit [file-name content]
  (let [fs (js/require "fs")]
    (.writeFileSync fs file-name content)))

(defn generate-html [ssr-stages]
  (let [ tree (comp-container schema/store ssr-stages)
         html-content (make-string tree)]
    (html-dsl {:build? true} html-content ssr-stages)))

(defn -main []
  (spit "target/index.html" (generate-html #{:shell})))

(-main)
