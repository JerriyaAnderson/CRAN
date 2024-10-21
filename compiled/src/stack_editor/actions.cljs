
(ns stack-editor.actions
  (:require [cljs.reader :refer [read-string]]
            [ajax.core :refer [GET POST PATCH json-request-format]]
            [stack-editor.util.querystring :refer [parse-query]]
            [shallow-diff.diff :refer [diff]]))

(def options
 (merge
   {"host" "localhost", "port" "7010"}
   (parse-query (.-search js/location))))

(defonce remote-sepal-ref (atom nil))

(defn load-collection! [dispatch!]
  (println (pr-str options))
  (GET
    (str "http://" (get options "host") ":" (get options "port"))
    {:error-handler
     (fn [error]
       (println error)
       (dispatch! :notification/add-one "failed to load collection")),
     :handler
     (fn [response]
       (println "response...")
       (let [sepal-data (read-string response)]
         (dispatch! :collection/load sepal-data)
         (reset! remote-sepal-ref sepal-data)))}))

(defn submit-changes! [collection dispatch!]
  (PATCH
    (str "http://localhost:" (get options "port"))
    {:format (json-request-format),
     :error-handler
     (fn [error]
       (println error)
       (dispatch! :notification/add-one "failed to post collection")),
     :body (pr-str (diff @remote-sepal-ref collection)),
     :handler
     (fn [response]
       (println response)
       (dispatch! :notification/add-one "saved")
       (reset! remote-sepal-ref collection))}))

(defn submit-collection! [collection dispatch!]
  (POST
    (str "http://localhost:" (get options "port"))
    {:format (json-request-format),
     :error-handler
     (fn [error]
       (println error)
       (dispatch! :notification/add-one "failed to post collection")),
     :body (pr-str collection),
     :handler
     (fn [response]
       (println response)
       (dispatch! :notification/add-one "patched")
       (reset! remote-sepal-ref collection))}))
