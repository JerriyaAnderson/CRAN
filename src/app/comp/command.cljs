
(ns app.comp.command
  (:require [clojure.string :as string]
            [respo.core :refer [defcomp div <> span input]]
            [hsl.core :refer [hsl]]))

(defn on-click [on-select] (fn [e dispatch!] (on-select dispatch!)))

(def style-command
  {:backgroud-color (hsl 0 0 0),
   :padding "0 8px",
   :line-height 2.4,
   :font-family "Source Code Pro, Menlo,monospace",
   :cursor "pointer"})

(defcomp
 comp-command
 (command selected? on-select)
 (div
  {:style (merge style-command (if selected? {:background-color (hsl 0 0 30)})),
   :on-click (on-click on-select)}
  (<> span (string/join " " command) nil)))
