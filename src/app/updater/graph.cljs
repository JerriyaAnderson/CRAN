
(ns app.updater.graph
  (:require (app.util.analyze :refer (parse-ns-deps pick-dep expand-deps-tree))
            (app.util.stack :refer (push-path))
            (clojure.set :refer (union difference))
            (app.util :refer (collect-defs))
            (clojure.string :as string)))

(defn edit-current [store op]
  (let [maybe-path (last (get-in store [:graph :path]))]
    (if (some? maybe-path)
      (-> store
          (update
           :writer
           (push-path
            {:ns (:ns maybe-path), :kind :defs, :extra (:def maybe-path), :focus []}))
          (assoc :router {:name :workspace, :data nil}))
      store)))

(defn load-graph [store op-data]
  (let [root-info (get-in store [:collection :root])
        files (get-in store [:collection :files])
        internal-ns (:ns root-info)
        ns-deps (parse-ns-deps (get-in files [internal-ns :ns]))
        def-expr (get-in files [(:ns root-info) :defs (:def root-info)])
        pkg (get-in store [:collection :package])
        this-file (get files internal-ns)
        deps-tree (expand-deps-tree internal-ns (:def root-info) files pkg #{})]
    (comment println ns-deps)
    (println)
    (-> store (assoc-in [:graph :tree] deps-tree))))

(defn show-orphans [store op-data]
  (let [all-defs (->> (get-in store [:collection :files])
                      (map
                       (fn [file-entry]
                         (let [ns-text (first file-entry)
                               defs (keys (:defs (val file-entry)))]
                           (->> defs
                                (map (fn [def-text] {:ns ns-text, :def def-text}))
                                (into #{})))))
                      (apply union))
        deps-tree (get-in store [:graph :tree])
        defs-in-tree (collect-defs deps-tree)]
    (update
     store
     :modal-stack
     (fn [xs] (conj xs {:title :orphans, :data (difference all-defs defs-in-tree)})))))

(defn view-ns [store op-data] (assoc-in store [:graph :ns-path] op-data))

(defn view-path [store op-data] (assoc-in store [:graph :path] op-data))
