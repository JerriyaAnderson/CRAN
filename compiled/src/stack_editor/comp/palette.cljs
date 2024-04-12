
(ns stack-editor.comp.palette
  (:require [clojure.string :as string]
            [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div input]]
            [respo-ui.style :as ui]
            [respo.comp.text :refer [comp-text]]
            [stack-editor.comp.command :refer [comp-command]]
            [stack-editor.util.keycode :as keycode]
            [stack-editor.actions :refer [submit-collection!]]
            [stack-editor.style.widget :as widget]))

(def basic-commands [["save"] ["load"]])

(defn init-state [& args] {:cursor 0, :text ""})

(def update-state merge)

(defn on-input [mutate!]
  (fn [e dispatch!] (mutate! {:cursor 0, :text (:value e)})))

(defn on-keydown [mutate! commands cursor collection]
  (fn [e dispatch!]
    (let [code (:key-code e) total (count commands)]
      (cond
        (= code keycode/key-esc) (dispatch! :router/toggle-palette nil)
        (= code keycode/key-down) (if
                                    (< cursor (dec total))
                                    (mutate! {:cursor (inc cursor)}))
        (= code keycode/key-up) (if
                                  (> cursor 0)
                                  (mutate! {:cursor (dec cursor)}))
        (= code keycode/key-enter) (let 
                                     [command
                                      (get (into [] commands) cursor)]
                                     (mutate! {:text ""})
                                     (dispatch!
                                       :router/toggle-palette
                                       nil)
                                     (case
                                       (first command)
                                       "load"
                                       (println "load")
                                       "save"
                                       (submit-collection!
                                         collection
                                         dispatch!)
                                       "definition"
                                       (dispatch!
                                         :collection/edit-definition
                                         (last command))
                                       "namespace"
                                       (dispatch!
                                         :collection/edit-namespace
                                         (last command))
                                       "procedure"
                                       (dispatch!
                                         :collection/edit-procedure
                                         (last command))
                                       nil))
        :else nil))))

(defn render [collection]
  (fn [state mutate!]
    (let [def-paths (->>
                      (keys (:definitions collection))
                      (map (fn [path] ["definition" path])))
          ns-names (->>
                     (keys (:namespaces collection))
                     (map (fn [ns-name] ["namespace" ns-name])))
          procedure-names (->>
                            (keys (:procedures collection))
                            (map
                              (fn [procedure-name] ["procedure"
                                                    procedure-name])))
          commands (->>
                     (concat
                       basic-commands
                       def-paths
                       ns-names
                       procedure-names)
                     (filter
                       (fn [command]
                         (some
                           (fn [piece]
                             (string/includes? piece (:text state)))
                           command))))]
      (div
        {:style
         (merge
           ui/fullscreen
           ui/row
           {:background-color (hsl 200 40 10 0.8),
            :justify-content "center",
            :position "fixed"})}
        (div
          {:style
           (merge
             ui/column
             {:background-color (hsl 0 0 0 0.8), :width "800px"})}
          (input
            {:style
             (merge widget/input {:line-height "40px", :width "100%"}),
             :event
             {:keydown
              (on-keydown mutate! commands (:cursor state) collection),
              :input (on-input mutate!)},
             :attrs
             {:placeholder "write command...",
              :value (:text state),
              :id "command-palette"}})
          (div
            {:style ui/flex}
            (->>
              commands
              (map-indexed
                (fn [idx command] [idx
                                   (comp-command
                                     command
                                     (= idx (:cursor state)))])))))))))

(def comp-palette (create-comp :palette init-state update-state render))
