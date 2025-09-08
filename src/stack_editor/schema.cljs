
(ns stack-editor.schema )

(def store
  {:router {:name :loading, :show-palette? false, :data :definitions},
   :notifications [],
   :writer {:pointer 0, :clipboard [], :stack [], :focus []},
   :modal-stack [],
   :collection {:definitions {}, :namespaces {}, :procedures {}}})
