
(ns stack-editor.updater.core
  (:require [stack-editor.updater.router :as router]
            [stack-editor.updater.collection :as collection]))

(defn default-handler [store op-data] store)

(defn updater [store op op-data]
  (let [handler (case
                  op
                  :router/route
                  router/route
                  :collection/add-definition
                  collection/add-definition
                  :collection/add-procedure
                  collection/add-procedure
                  :collection/add-namespace
                  collection/add-namespace
                  :collection/set-main
                  collection/set-main
                  :collection/edit
                  collection/edit
                  :collection/write
                  collection/write-code
                  :collection/edit-procedure
                  collection/edit-procedure
                  :collection/edit-namespace
                  collection/edit-namespace
                  default-handler)]
    (handler store op-data)))
