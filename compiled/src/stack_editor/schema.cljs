
(ns stack-editor.schema)

(def store
 {:router {:name :home, :data nil},
  :notifications [],
  :writer {:pointer nil, :stack [], :focus []},
  :collection {}})
