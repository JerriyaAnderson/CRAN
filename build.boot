
(set-env!
  :asset-paths #{"assets/"}
 :resource-paths #{"src"}
 :dependencies '[[org.clojure/clojure       "1.8.0"       :scope "test"]
                 [org.clojure/clojurescript "1.9.521"     :scope "test"]
                 [adzerk/boot-cljs          "1.7.228-1"   :scope "test"]
                 [adzerk/boot-reload        "0.4.13"      :scope "test"]
                 [cirru/boot-stack-server   "0.1.29"      :scope "test"]
                 [adzerk/boot-test          "1.1.2"       :scope "test"]
                 [mvc-works/hsl             "0.1.2"]
                 [respo                     "0.3.38"]
                 [respo/ui                  "0.1.6"]
                 [cirru/editor              "0.1.20"]
                 [respo/border              "0.1.0"]
                 [cumulo/shallow-diff       "0.1.1"]
                 [cljs-ajax                 "0.5.8"]])

(require '[adzerk.boot-cljs   :refer [cljs]]
         '[adzerk.boot-reload :refer [reload]])

(deftask dev []
  (set-env!
    :asset-paths #{"assets"})
  (comp
    (watch)
    (reload :on-jsload 'stack-editor.main/on-jsload!
            :cljs-asset-path ".")
    (cljs :compiler-options {:language-in :ecmascript5})
    (target :no-clean true)))

(deftask build-advanced []
  (set-env!
    :asset-paths #{"assets"})
  (comp
    (cljs :optimizations :advanced
          :compiler-options {:language-in :ecmascript5
                             :pseudo-names true
                             :static-fns true
                             :parallel-build true
                             :optimize-constants true
                             :source-map true})
    (target :no-clean true)))

(deftask build-server []
  (set-env!
    :resource-paths #{"server-src/"})
  (comp
    ; (watch)
    (cljs :optimizations :simple
          :compiler-options {:language-in :ecmascript5
                             :target :nodejs
                             :parallel-build true})
    (target :no-clean true :dir #{"server-target"})))

(def +version+ "0.1.0")

(deftask build []
  (comp
    (pom :project     'Cirru/stack-editor
         :version     +version+
         :description "Stack editor"
         :url         "https://github.com/Cirru/stack-editor"
         :scm         {:url "https://github.com/Cirru/stack-editor"}
         :license     {"MIT" "http://opensource.org/licenses/mit-license.php"})
    (jar)
    (install)
    (target)))

(deftask deploy []
  (set-env!
    :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"}]))
  (comp
    (build)
    (push :repo "clojars" :gpg-sign (not (.endsWith +version+ "-SNAPSHOT")))))
