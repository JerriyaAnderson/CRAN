
(ns app.comp.file-tree
  (:require [hsl.core :refer [hsl]]
            [respo.core :refer [defcomp div list-> >> <> span input button]]
            [respo-ui.core :as ui]
            [clojure.string :as string]
            [respo.comp.space :refer [=<]]
            [app.comp.brief-file :refer [comp-brief-file]]
            [app.style.widget :as widget]
            [app.util.keycode :as keycode]))

(defn on-keydown [draft cursor]
  (fn [e dispatch!]
    (if (= keycode/key-enter (.-keyCode (:original-event e)))
      (do
       (if (string/includes? draft "/")
         (dispatch! :collection/add-definition (string/split draft "/"))
         (dispatch! :collection/add-namespace draft))
       (dispatch! cursor {:draft ""})))))

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
    {:style (merge ui/expand ui/column style-file-tree)}
    (div
     {:style (merge ui/row style-body)}
     (div
      {:style ui/column}
      (input
       {:value (:draft state),
        :placeholder "ns/def or ns",
        :style widget/input,
        :on-input (fn [e dispatch!] (dispatch! cursor (assoc state :draft (:value e)))),
        :on-keydown (on-keydown (:draft state) cursor)})
      (list->
       {:style {:overflow :auto, :padding "8px 0px 200px 0px"}}
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
                 (<> ns-name))])))))
     (=< 8 nil)
     (if (contains? files selected-ns)
       (comp-brief-file (>> states selected-ns) selected-ns (get files selected-ns)))))))
