
(ns stack-editor.schema)

(def store
 {:router {:name :analyzer, :data :definitions},
  :notifications [],
  :writer
  {:pointer [:definitions nil], :clipboard [], :stack [], :focus []},
  :collection {:definitions {}, :namespaces {}, :procedures {}}})
