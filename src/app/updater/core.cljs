
(ns app.updater.core
  (:require [respo.cursor :refer [mutate]]
            [app.updater.router :as router]
            [app.updater.collection :as collection]
            [app.updater.notification :as notification]
            [app.updater.stack :as stack]
            [app.updater.modal :as modal]
            (app.updater.graph :as graph)))

(defn default-handler [store op-data] store)

(defn updater [store op op-data op-id]
  (let [handler (case op
                  :states (fn [x] (update x :states (mutate op-data)))
                  :router/route router/route
                  :router/toggle-palette router/toggle-palette
                  :collection/add-definition collection/add-definition
                  :collection/add-namespace collection/add-namespace
                  :collection/edit collection/edit
                  :collection/edit-ns collection/edit-ns
                  :collection/write collection/write-code
                  :collection/load collection/load-remote
                  :collection/remove-this collection/remove-this
                  :collection/rename collection/rename
                  :collection/hydrate collection/hydrate
                  :notification/add-one notification/add-one
                  :notification/remove-one notification/remove-one
                  :notification/remove-since notification/remove-since
                  :stack/goto-definition stack/goto-definition
                  :stack/dependents stack/dependents
                  :stack/go-back stack/go-back
                  :stack/go-next stack/go-next
                  :stack/point-to stack/point-to
                  :stack/collapse stack/collapse
                  :stack/shift stack/shift-one
                  :modal/mould modal/mould
                  :modal/recycle modal/recycle
                  :graph/load-graph graph/load-graph
                  :graph/view-path graph/view-path
                  :graph/view-ns graph/view-ns
                  :graph/edit-current graph/edit-current
                  default-handler)]
    (handler store op-data op-id)))
