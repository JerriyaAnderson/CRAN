
(ns stack-editor.updater.core
  (:require [stack-editor.updater.router :as router]))

(defn default-handler [store op-data] store)

(defn updater [store op op-data]
  (let [handler (case op :router/route router/route default-handler)]
    (handler store op-data)))
