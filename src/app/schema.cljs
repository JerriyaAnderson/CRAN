
(ns app.schema )

(def store
  {:router {:name :loading, :data :definitions, :show-palette? false},
   :collection {:package nil, :files {}},
   :writer {:stack [], :pointer 0, :clipboard []},
   :notifications [],
   :modal-stack [],
   :states {}})
