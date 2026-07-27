
(ns stack-editor.main
  (:require        [cljs.reader :refer [read-string]]
                   [cljs.core.async :refer [<! >! timeout chan]]
                   [shallow-diff.patch :refer [patch]]
                   [stack-server.analyze :refer [generate-file ns->path]]
                   [fipp.edn :refer [pprint]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def fs (js/require "fs"))
(def http (js/require "http"))
(def path (js/require "path"))

(def ir-path (or (get (js->clj js/process.argv) 2) "stack-sepal.ir"))
(def out-folder (or js/process.env.out "src/"))
(def extension (or js/process.env.extension ".cljs"))
(def port (js/parseInt (or js/process.env.port "7010")))

(def ref-sepal
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
  (let [file-name (str (ns->path pkg ns-part) extension)
        content (generate-file ns-part file-info)]
    (println "File compiled:" file-name)
    (fs.writeFileSync (path.join out-folder file-name) content)))

(defn compare-write-source! [sepal-data]
  (doseq [entry (:files sepal-data)]
    (let [[ns-part file-info] entry
          changed? (not (identical? file-info (get-in @ref-sepal [:files ns-part])))]
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
    "GET" (.end res (pr-str @ref-sepal))
    "POST"
      (go (let [content (<! (read-body req))
                new-data (read-string content)]
            (compare-write-source! new-data)
            (.end res (pr-str {:status "ok"}))
            (rewrite-file! new-data)
            (reset! ref-sepal new-data)))
    "PATCH"
      (go (let [changes-content (<! (read-body req))
                new-data (patch @ref-sepal (read-string changes-content))]
            (compare-write-source! new-data)
            (.end res (pr-str {:status "ok"}))
            (rewrite-file! new-data)
            (reset! ref-sepal new-data)))
    (.end res (str "Unknown:" req.method))))

(defn create-app! []
  (let [app (http.createServer req-handler)]
    (.listen app port)
    (println (str "File: " ir-path))
    (println (str "Port: " port))
    (println (str "Output: " out-folder))
    (println (str "Extension: " extension))
    (println "Edit with http://repo.cirru.org/stack-editor/?port=7010")))

(defn -main []
  (if (= js/process.env.op "compile")
    (compile-source! @ref-sepal)
    (create-app!)))

(enable-console-print!)
(-main)
