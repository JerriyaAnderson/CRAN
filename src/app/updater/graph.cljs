
(ns app.updater.graph
  (:require (app.util.analyze :refer (parse-ns-deps pick-dep pick-def-deps expand-deps-tree))))

(defn load-graph [store op-data]
  (let [root-info (get-in store [:collection :root])
        files (get-in store [:collection :files])
        internal-ns (:ns root-info)
        ns-deps (parse-ns-deps (get-in files [internal-ns :ns]))
        def-expr (get-in files [(:ns root-info) :defs (:def root-info)])
        pkg (get-in store [:collection :package])
        this-file (get files internal-ns)
        deps-tree (expand-deps-tree internal-ns (:def root-info) files pkg #{})]
    (comment println (pick-def-deps def-expr internal-ns this-file pkg))
    (comment println ns-deps)
    (println)
    (assoc-in store [:graph :tree] deps-tree)))
