
(ns app.comp.rename-path
  (:require [respo.core :refer [defcomp div <> span input]]
            [respo.comp.space :refer [=<]]
            [respo-ui.core :as ui]
            [app.style.widget :as widget]
            [app.util.keycode :as keycode]))

(defn init-state [code-path]
  (let [{ns-part :ns, kind :kind, extra-name :extra} code-path]
    (if (= kind :defs) (str ns-part "/" extra-name) ns-part)))

(defn on-input [e dispatch! m!] (m! (:value e)))

(defn on-keydown [code-path text]
  (fn [e d! m!]
    (println keycode/key-esc)
    (cond
      (= (:key-code e) keycode/key-enter)
        (do (d! :collection/rename [code-path text]) (d! :modal/recycle nil) (m! nil))
      (= (:key-code e) keycode/key-esc) (d! :modal/recycle nil)
      :else nil)))

(defn on-rename [code-path text]
  (fn [e d! m!] (d! :collection/rename [code-path text]) (d! :modal/recycle nil) (m! nil)))

(defcomp
 comp-rename-path
 (states code-path)
 (let [state (or (:data states) (init-state code-path))]
   (div
    {}
    (div {} (<> span (str "Rename: " (:ns code-path) "/" (:extra code-path)) nil))
    (div
     {}
     (input
      {:value state,
       :id "rename-box",
       :style (merge ui/input {:width 400}),
       :on-input on-input,
       :on-keydown (on-keydown code-path state)})
     (=< 16 nil)
     (div
      {:inner-text "Rename", :style widget/button, :on-click (on-rename code-path state)})))))
