
(ns app.comp.loading
  (:require-macros [respo.macros :refer [defcomp <> span div]])
  (:require [hsl.core :refer [hsl]]
            [respo.core :refer [create-comp]]
            [respo-ui.style :as ui]))

(def style-loading
  {:background-color (hsl 200 40 10),
   :justify-content "center",
   :color (hsl 0 0 80),
   :font-size "32px",
   :font-weight "100",
   :font-family "Josefin Sans"})

(defcomp
 comp-loading
 ()
 (div {:style (merge ui/fullscreen ui/row-center style-loading)} (<> span "Loading..." nil)))
