
(ns cirru.stack-server
  (:require        [cljs.reader :refer [read-string]]
                   [cljs.core.async :refer [<! >! timeout chan]]
                   [shallow-diff.patch :refer [patch]]
                   [stack-server.analyze :refer [collect-files]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def fs (js/require "fs"))
(def http (js/require "http"))
(def path (js/require "path"))

(def ir-path "stack-sepal.ir")
(def out-folder "src/")
(def extname ".cljs")

(def sepal-ref
  (atom (read-string (fs.readFileSync ir-path "utf8"))))

(defn read-body [req]
  (let [body-ref (atom "")
        body-chan (chan)]
    (.on req "data" (fn [chunk] (swap! body-ref str chunk)))
    (.on req "end" (fn [] (go (>! body-chan @body-ref))))
    body-chan))

(defn rewrite-file! [content]
  (fs.writeFileSync ir-path content))

(defn write-source! [sepal-data]
  (let [file-dict (collect-files sepal-data)]
    (doseq [entry file-dict]
      (let [[file-name content] entry]
        (println "File compiled:" file-name)
        (fs.writeFileSync (path.join out-folder (str file-name extname)) content)))))

(defn req-handler [req res]
  (.setHeader res "Access-Control-Allow-Origin" req.headers.origin)
  (.setHeader res "Content-Type" "text/edn; charset=UTF-8")
  (.setHeader res "Access-Control-Allow-Methods" "GET, POST, PATCH, OPTIONS")
  (case req.method
    "GET" (.end res (pr-str @sepal-ref))
    "POST"
      (go (let [content (<! (read-body req))
                new-data (read-string content)]
            (reset! sepal-ref new-data)
            (write-source! new-data)
            (.end res (pr-str {:status "ok"}))
            (rewrite-file! content)))
    "PATCH"
      (go (let [changes-content (<! (read-body req))
                new-data (patch @sepal-ref (read-string changes-content))]
            (reset! sepal-ref new-data)
            (write-source! new-data)
            (.end res (pr-str {:status "ok"}))
            (rewrite-file! (pr-str new-data))))
    (.end res (str "Unknown:" req.method))))

(defn create-app! []
  (let [app (http.createServer req-handler)]
    (.listen app 7010)
    (println "App listening on 7010.")))

(create-app!)
(write-source! @sepal-ref)
