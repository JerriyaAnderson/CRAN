
(ns app.comp.file-tree
  (:require [hsl.core :refer [hsl]]
            [respo.core :refer [defcomp div list-> >> <> span input button]]
            [respo-ui.core :as ui]
            [clojure.string :as string]
            [respo.comp.space :refer [=<]]
            [app.comp.brief-file :refer [comp-brief-file]]
            [app.style.widget :as widget]
            [app.util.keycode :as keycode]))

(defn on-change [cursor] (fn [e dispatch!] (dispatch! :states [cursor (:value e)])))

(defn on-graph [e dispatch!] (dispatch! :router/route {:name :graph, :data nil}))

(defn on-keydown [draft cursor]
  (fn [e dispatch!]
    (if (= keycode/key-enter (.-keyCode (:original-event e)))
      (do
       (if (string/includes? draft "/")
         (dispatch! :collection/add-definition (string/split draft "/"))
         (dispatch! :collection/add-namespace draft))
       (dispatch! cursor {:draft ""})))))

(defn on-stack [e d! m!] (d! :router/route {:name :workspace, :data nil}))

(def style-toolbar {:padding "16px 16px"})

(defn render-toolbar [draft cursor]
  (div
   {:style style-toolbar}
   (button {:inner-text "Graph", :style widget/button, :on-click on-graph})
   (=< 8 nil)
   (button {:inner-text "Stack", :style widget/button, :on-click on-stack})
   (=< 8 nil)
   (input
    {:value draft,
     :placeholder "ns/def or ns",
     :style widget/input,
     :on-input (on-change cursor),
     :on-keydown (on-keydown draft cursor)})))

(def style-body {:flex 1, :overflow :auto})

(def style-file-tree {:background-color (hsl 0 0 0), :padding "0 16px"})

(defcomp
 comp-file-tree
 (states store)
 (let [cursor (:cursor states)
       state (or (:data states) {:draft "", :selected-ns nil})
       files (get-in store [:collection :files])
       selected-ns (:selected-ns state)]
   (div
    {:style (merge ui/fullscreen ui/column style-file-tree)}
    (render-toolbar (:draft state) cursor)
    (div
     {:style (merge ui/row style-body)}
     (list->
      {:style {:overflow :auto, :padding "16px 16px 200px 16px"}}
      (->> (keys files)
           (sort)
           (map
            (fn [ns-name]
              [ns-name
               (div
                {:on-click (fn [e d!] (d! cursor (assoc state :selected-ns ns-name))),
                 :style (merge
                         {:color (hsl 0 0 50), :cursor :pointer}
                         (if (= ns-name selected-ns) {:color (hsl 0 0 100)}))}
                (<> ns-name))]))))
     (if (contains? files selected-ns)
       (comp-brief-file (>> states selected-ns) selected-ns (get files selected-ns)))))))
