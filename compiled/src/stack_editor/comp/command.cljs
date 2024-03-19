
(ns stack-editor.comp.command
  (:require [clojure.string :as string]
            [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div]]
            [respo.comp.text :refer [comp-text]]))

(defn render [command selected?]
  (fn [state mutate!]
    (div
      {:style
       (merge
         {:line-height 2.4,
          :backgroud-color (hsl 0 0 0),
          :padding "0 8px",
          :font-family "Menlo,monospace"}
         (if selected? {:background-color (hsl 0 0 30)}))}
      (comp-text (string/join " " command) nil))))

(def comp-command (create-comp :command render))
