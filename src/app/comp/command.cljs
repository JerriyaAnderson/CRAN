
(ns app.comp.command
  (:require-macros (respo.macros :refer [defcomp <> div span]))
  (:require [clojure.string :as string]
            [hsl.core :refer [hsl]]
            [respo.core :refer [create-comp]]))

(def style-command
  {:backgroud-color (hsl 0 0 0),
   :padding "0 8px",
   :line-height 2.4,
   :font-family "Source Code Pro, Menlo,monospace",
   :cursor "pointer"})

(defn on-click [on-select] (fn [e dispatch!] (on-select dispatch!)))

(defcomp
 comp-command
 (command selected? on-select)
 (div
  {:style (merge style-command (if selected? {:background-color (hsl 0 0 30)})),
   :event {:click (on-click on-select)}}
  (<> span (string/join " " command) nil)))
