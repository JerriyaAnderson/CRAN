
(ns stack-editor.schema)

(def store
 {:router {:name :home, :data nil},
  :notifications [],
  :snapshot {:tree [], :focus [], :entry nil},
  :writer {:pointer nil, :stack []}})
