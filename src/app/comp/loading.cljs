
(ns app.comp.loading
  (:require [hsl.core :refer [hsl]]
            [respo.core :refer [defcomp div <> span input]]
            [respo-ui.core :as ui]))

(def style-loading
  {:background-color (hsl 200 40 10),
   :justify-content "center",
   :color (hsl 0 0 80),
   :font-size "32px",
   :font-weight "100",
   :font-family ui/font-fancy})

(defcomp
 comp-loading
 ()
 (div {:style (merge ui/fullscreen ui/row-center style-loading)} (<> "Loading..." nil)))
