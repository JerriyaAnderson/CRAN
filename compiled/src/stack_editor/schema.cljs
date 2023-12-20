
(ns stack-editor.schema)

(def store
 {:router {:name :analyzer, :data :definitions},
  :notifications [],
  :writer {:clipboard [], :kind :definitions, :stack [], :focus []},
  :collection
  {:definitions {},
   :namespaces {},
   :main-definition nil,
   :procedures {}}})
