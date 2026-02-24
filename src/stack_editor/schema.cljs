
(ns stack-editor.schema )

(def store
  {:router {:name :loading, :data :definitions, :show-palette? false},
   :collection {:package nil, :files {}},
   :writer {:stack [], :pointer 0, :focus [], :clipboard []},
   :notifications [],
   :modal-stack []})
