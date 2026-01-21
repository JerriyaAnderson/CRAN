
(set-env!
 :dependencies '[[org.clojure/clojure       "1.8.0"       :scope "test"]
                 [org.clojure/clojurescript "1.9.293"     :scope "test"]
                 [adzerk/boot-cljs          "1.7.228-1"   :scope "test"]
                 [adzerk/boot-reload        "0.4.13"      :scope "test"]
                 [cirru/boot-stack-server   "0.1.24"      :scope "test"]
                 [adzerk/boot-test          "1.1.2"       :scope "test"]
                 [mvc-works/hsl             "0.1.2"]
                 [respo                     "0.3.33"]
                 [respo/ui                  "0.1.6"]
                 [cirru/editor              "0.1.18"]
                 [respo/border              "0.1.0"]
                 [cumulo/shallow-diff       "0.1.1"]
                 [cljs-ajax                 "0.5.8"]])

(require '[adzerk.boot-cljs   :refer [cljs]]
         '[adzerk.boot-reload :refer [reload]]
         '[stack-server.core  :refer [start-stack-editor! transform-stack]]
         '[respo.alias        :refer [html head title script style meta' div link body]]
         '[respo.render.html  :refer [make-html]]
         '[adzerk.boot-test   :refer :all]
         '[clojure.java.io    :as    io])

(def +version+ "0.1.0")

(task-options!
  pom {:project     'Cirru/stack-editor
       :version     +version+
       :description "Stack editor"
       :url         "https://github.com/Cirru/stack-editor"
       :scm         {:url "https://github.com/Cirru/stack-editor"}
       :license     {"MIT" "http://opensource.org/licenses/mit-license.php"}})

(defn html-dsl [data fileset]
  (make-html
    (html {}
      (head {}
        (title {:attrs {:innerHTML "Stack Editor"}})
        (link {:attrs {:rel "icon" :type "image/png" :href "http://logo.cirru.org/cirru-400x400.png"}})
        (link {:attrs {:rel "stylesheet" :href "style.css"}})
        (meta '{:attrs {:charset "utf-8"}})
        (meta' {:attrs {:name "viewport" :content "width=device-width, initial-scale=1"}})
        (meta' {:attrs {:id "ssr-stages" :content "#{}"}})
        (style {:attrs {:innerHTML "body {margin: 0;}"}})
        (style {:attrs {:innerHTML "body * {box-sizing: border-box;}"}})
        (script {:attrs {:id "config" :type "text/edn" :innerHTML (pr-str data)}}))
      (body {}
        (div {:attrs {:id "app"}})
        (script {:attrs {:src "main.js"}})))))

(deftask html-file
  "task to generate HTML file"
  [d data VAL edn "data piece for rendering"]
  (with-pre-wrap fileset
    (let [tmp (tmp-dir!)
          out (io/file tmp "dev.html")]
      (empty-dir! tmp)
      (spit out (html-dsl data fileset))
      (-> fileset
        (add-resource tmp)
        (commit!)))))

(deftask editor! []
  (comp
    (wait)
    (start-stack-editor!)
    (target :dir #{"src/"})))

(deftask dev! []
  (set-env!
    :asset-paths #{"assets"})
  (comp
    (editor!)
    (html-file :data {:build? false})
    (reload :on-jsload 'stack-editor.main/on-jsload!
            :cljs-asset-path ".")
    (cljs :compiler-options {:language-in :ecmascript5})
    (target :no-clean true)))

(deftask generate-code []
  (comp
    (transform-stack :filename "stack-sepal.ir")
    (target :dir #{"src/"})))

(deftask build-advanced []
  (set-env!
    :asset-paths #{"assets"})
  (comp
    (transform-stack :filename "stack-sepal.ir")
    (cljs :optimizations :advanced
          :compiler-options {:language-in :ecmascript5
                             :pseudo-names true
                             :static-fns true
                             :parallel-build true
                             :optimize-constants true
                             :source-map true})
    (html-file :data {:build? true})
    (target)))

(deftask rsync []
  (with-pre-wrap fileset
    (sh "rsync" "-r" "target/" "cirru.org:repo/Cirru/stack-editor" "--exclude" "main.out" "--delete")
    fileset))

(deftask build []
  (comp
    (transform-stack :filename "stack-sepal.ir")
    (pom)
    (jar)
    (install)
    (target)))

(deftask deploy []
  (set-env!
    :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"}]))
  (comp
    (build)
    (push :repo "clojars" :gpg-sign (not (.endsWith +version+ "-SNAPSHOT")))))

(deftask watch-test []
  (set-env!
    :source-paths #{"cirru/src" "cirru/test"})
  (comp
    (watch)
    (test :namespaces '#{stack-editor.test})))
