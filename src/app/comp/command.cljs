
(ns app.comp.command
  (:require [clojure.string :as string]
            [respo.core :refer [defcomp div <> span input]]
            [hsl.core :refer [hsl]]
            [respo.comp.space :refer [=<]]
            [respo-ui.core :as ui]))

(def style-command
  {:backgroud-color (hsl 0 0 0),
   :padding "0 8px",
   :line-height "30px",
   :font-family ui/font-normal,
   :cursor "pointer"})

(defcomp
 comp-command
 (command selected? on-select)
 (div
  {:style (merge style-command (if selected? {:background-color (hsl 0 0 20 0.8)})),
   :on-click (fn [e dispatch!] (on-select dispatch!))}
  (case (first command)
    :defs (div {} (<> (get command 2)) (=< 16 nil) (<> (get command 1) {:color (hsl 0 0 40)}))
    (<> (string/join " " command) nil))))
