
(ns cirru.stack-server
  (:require        [cljs.reader :refer [read-string]]
                   [cljs.core.async :refer [<! >! timeout chan]]
                   [shallow-diff.patch :refer [patch]]
                   [stack-server.analyze :refer [generate-file ns->path]]
                   [fipp.edn :refer [pprint]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def fs (js/require "fs"))
(def http (js/require "http"))
(def path (js/require "path"))

(def ir-path "stack-sepal.ir")
(def out-folder "src/")
(def extname ".cljs")
(def port 7010)

(def sepal-ref
  (atom (read-string (fs.readFileSync ir-path "utf8"))))

(defn read-body [req]
  (let [body-ref (atom "")
        body-chan (chan)]
    (.on req "data" (fn [chunk] (swap! body-ref str chunk)))
    (.on req "end" (fn [] (go (>! body-chan @body-ref))))
    body-chan))

(defn rewrite-file! [content]
  (fs.writeFileSync ir-path (with-out-str (pprint content {:width 120}))))

(defn write-by-file [pkg ns-part file-info]
  (let [file-name (str (ns->path pkg ns-part) extname)
        content (generate-file ns-part file-info)]
    (println "File compiled:" file-name)
    (fs.writeFileSync (path.join out-folder file-name) content)))

(defn compare-write-source! [sepal-data]
  (doseq [entry (:files sepal-data)]
    (let [[ns-part file-info] entry
          changed? (not (identical? file-info (get-in @sepal-ref [:files ns-part])))]
      (if changed?
        (write-by-file (:package sepal-data) ns-part file-info)))))

(defn compile-source! [sepal-data]
  (doseq [entry (:files sepal-data)]
    (let [[ns-part file-info] entry]
      (write-by-file (:package sepal-data) ns-part file-info))))

(defn req-handler [req res]
  (if (some? req.headers.origin)
    (.setHeader res "Access-Control-Allow-Origin" req.headers.origin))
  (.setHeader res "Content-Type" "text/edn; charset=UTF-8")
  (.setHeader res "Access-Control-Allow-Methods" "GET, POST, PATCH, OPTIONS")
  (case req.method
    "GET" (.end res (pr-str @sepal-ref))
    "POST"
      (go (let [content (<! (read-body req))
                new-data (read-string content)]
            (compare-write-source! new-data)
            (.end res (pr-str {:status "ok"}))
            (rewrite-file! new-data)
            (reset! sepal-ref new-data)))
    "PATCH"
      (go (let [changes-content (<! (read-body req))
                new-data (patch @sepal-ref (read-string changes-content))]
            (compare-write-source! new-data)
            (.end res (pr-str {:status "ok"}))
            (rewrite-file! new-data)
            (reset! sepal-ref new-data)))
    (.end res (str "Unknown:" req.method))))

(defn create-app! []
  (let [app (http.createServer req-handler)]
    (.listen app port)
    (println (str "App listening on " port "."))))

(if (= js/process.env.op "compile")
  (compile-source! @sepal-ref)
  (create-app!))
