
(ns stack-editor.comp.palette
  (:require [clojure.string :as string]
            [hsl.core :refer [hsl]]
            [respo.alias :refer [create-comp div input]]
            [respo-ui.style :as ui]
            [respo.comp.text :refer [comp-text]]
            [cirru-editor.util.dom :refer [focus!]]
            [stack-editor.comp.command :refer [comp-command]]
            [stack-editor.util.keycode :as keycode]
            [stack-editor.style.widget :as widget]
            [stack-editor.util.detect :refer [fuzzy-search]]))

(defn on-input [cursor state]
  (fn [e dispatch!] (dispatch! :states [cursor (merge state {:text (:value e), :cursor 0})])))

(defn handle-command [cursor commands files dispatch!]
  (let [command (get (into [] commands) cursor)]
    (println "Command" (pr-str command))
    (dispatch! :router/toggle-palette nil)
    (case (first command)
      :load (dispatch! :effect/load nil)
      :patch (dispatch! :effect/submit true)
      :dehydrate (dispatch! :effect/dehydrate nil)
      :hydrate (dispatch! :modal/mould {:title :hydrate, :data nil})
      :defs
        (do
         (dispatch!
          :collection/edit
          {:ns (get command 1), :kind :defs, :extra (last command), :focus []}))
      :ns
        (do
         (dispatch!
          :collection/edit
          {:ns (get command 1), :kind :ns, :extra nil, :focus []}))
      :procs
        (do
         (dispatch!
          :collection/edit
          {:ns (get command 1), :kind :procs, :extra nil, :focus []}))
      nil)))

(def style-container
  {:position "fixed", :background-color (hsl 200 40 10 0.8), :justify-content "center"})

(defn on-select [cursor commands files]
  (fn [dispatch!] (handle-command cursor commands files dispatch!)))

(def initial-state {:text "", :cursor 0})

(def basic-commands [[:save] [:load] [:hydrate] [:dehydrate]])

(defn on-keydown [respo-cursor state commands cursor collection]
  (fn [e dispatch!]
    (let [code (:key-code e), total (count commands)]
      (cond
        (= code keycode/key-esc)
          (do
           (dispatch! :states [respo-cursor (merge state {:text ""})])
           (dispatch! :router/toggle-palette nil)
           (focus!))
        (= code keycode/key-down)
          (if (< cursor (dec total))
            (dispatch! :states [respo-cursor (merge state {:cursor (inc cursor)})]))
        (= code keycode/key-up)
          (if (> cursor 0)
            (dispatch! :states [respo-cursor (merge state {:cursor (dec cursor)})]))
        (= code keycode/key-enter)
          (do
           (dispatch! :states [respo-cursor (merge state {:text ""})])
           (handle-command cursor commands collection dispatch!))
        :else nil))))

(def comp-palette
  (create-comp
   :palette
   (fn [states files]
     (fn [cursor]
       (let [ns-names (->> (keys files) (map (fn [path] [:ns path])))
             state (or (:data states) initial-state)
             def-paths (->> files
                            (map
                             (fn [entry]
                               (let [[ns-part tree] entry]
                                 (->> (:defs tree)
                                      (keys)
                                      (map (fn [def-name] [:defs ns-part def-name]))))))
                            (apply concat))
             procedure-names (->> (keys files) (map (fn [proc-name] [:procs proc-name])))
             queries (string/split (:text state) " ")
             commands (->> (concat def-paths ns-names procedure-names basic-commands)
                           (filter (fn [command] (fuzzy-search command queries))))]
         (div
          {:style (merge ui/fullscreen ui/row style-container)}
          (div
           {:style (merge ui/column {:background-color (hsl 0 0 0 0.8), :width "800px"})}
           (input
            {:style (merge widget/input {:width "100%", :line-height "40px"}),
             :attrs {:placeholder "write command...",
                     :id "command-palette",
                     :value (:text state)},
             :event {:input (on-input cursor state),
                     :keydown (on-keydown cursor state commands (:cursor state) files)}})
           (div
            {:style (merge ui/flex {:overflow "auto"})}
            (->> commands
                 (map-indexed
                  (fn [idx command]
                    [idx
                     (comp-command
                      command
                      (= idx (:cursor state))
                      (on-select idx commands files))])))))))))))
