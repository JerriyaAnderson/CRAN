(ns app.server
  (:require        [cljs.reader :refer [read-string]]
                   [clojure.string :as string]
                   [clojure.set :refer [difference]]
                   [cljs.core.async :refer [<! >! timeout chan]]
                   [shallow-diff.patch :refer [patch]]
                   [cirru-sepal.analyze :refer [write-file ns->path]]
                   [cirru-edn.core :as cirru-edn]
                   [fipp.edn :refer [pprint]]
                   [app.walk :refer [walk]]
                   ["fs" :as fs]
                   ["http" :as http]
                   ["path" :as path]
                   ["mkdirp" :as mkdirp])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def ir-path
  (or
    (get (js->clj js/process.argv) 2)
    (if (fs/existsSync "stack.cirru") "stack.cirru")
    (do
      (println "Missing file: stack.cirru not found!")
      (.exit js/process 1))))

(def ref-sepal
  (atom
    (if (fs/existsSync ir-path)
        (cirru-edn/parse (fs/readFileSync ir-path "utf8"))
        (do (.log js/console (str "Error: " ir-path " does not exist!"))
            (.exit js/process 1)))))

(def extension (or js/process.env.extension (:extension @ref-sepal) ".cljs"))
(def out-folder (or js/process.env.out (get-in @ref-sepal [:options :src]) "src/"))
(def port (js/parseInt (or js/process.env.port (get-in @ref-sepal [:options :port]) "7010")))

(defn read-body [^js req]
  (let [body-ref (atom "")
        body-chan (chan)]
    (.on req "data" (fn [chunk] (swap! body-ref str chunk)))
    (.on req "end" (fn [] (go (>! body-chan @body-ref))))
    body-chan))

(defn rewrite-file! [content]
  (fs/writeFileSync ir-path (cirru-edn/write content)))

(defn write-by-file [pkg ns-part file-info]
  (let [file-name (str (ns->path pkg ns-part) extension)
        content (write-file file-info)
        file-target (path/join out-folder file-name)
        container (path/dirname file-target)]
    (println "File compiled:" file-name)
    (if (not (fs/existsSync container))
        (do
          (println "Creating folder:" container)
          (mkdirp/sync container)))
    (fs/writeFileSync file-target content)))

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

(defn req-handler [^js req ^js res]
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
  (let [app (http/createServer req-handler)]
    (.listen app port)
    (println "File:" ir-path)
    (println "Port:" port)
    (println "Output:" out-folder)
    (println "Extension:" extension)
    (println "Version: 0.2.9")
    (println (str "Edit with http://stack-editor.cirru.org/?port=" port))))

(defn main! []
  (if (= js/process.env.op "compile")
    (compile-source! @ref-sepal)
    (create-app!)))

(defn get-entries [sepal-data]
  (map
    (fn [ns-part]
      (string/replace
        (path/join out-folder
                   (string/replace (:package @ref-sepal) "." "/")
                   (str (string/replace ns-part "." "/") extension))
        "-" "_"))
    (keys (:files sepal-data))))

(defn check-removed! []
  (let [*files (atom [])
        collect! (fn [x] (swap! *files conj x))
        ns-entries (get-entries @ref-sepal)]
    (walk out-folder collect!)
    (println)
    (let [alive-files (into #{} ns-entries)
          existing-files (into #{} @*files)
          removed-files (difference existing-files alive-files)]
      ; (println "alive-files" alive-files)
      ; (println "existing-files" existing-files)
      (doseq [file-path removed-files]
        (fs/unlinkSync file-path)
        (println "Redundant file:" file-path)))))

(.on js/process "SIGINT"
  (fn []
    (if (fs/existsSync out-folder) (check-removed!))
    (.exit js/process)))

(defn reload! []
  (println "reload! not implemented!"))
