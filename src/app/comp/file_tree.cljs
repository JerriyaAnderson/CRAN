
(ns app.comp.file-tree
  (:require-macros (respo.macros :refer (defcomp)))
  (:require [hsl.core :refer [hsl]]
            [respo.alias :refer [div]]
            [respo.comp.text :refer [comp-text]]
            [respo-ui.style :as ui]
            (clojure.string :as string)
            (app.util :refer (segments->tree))
            (respo.comp.space :refer (comp-space))
            (app.comp.brief-file :refer (comp-brief-file))))

(def style-file-tree {:background-color (hsl 0 0 0)})

(defn render-toolbar [] (div {} (comp-text "Tools" nil)))

(defn on-view [path ns-piece]
  (fn [e dispatch!] (dispatch! :graph/view-ns (conj path ns-piece))))

(def style-file {:cursor :pointer})

(def style-column {:padding "16px 16px", :min-width 80, :line-height 1.4})

(defcomp
 comp-file-tree
 (store)
 (let [ns-path (get-in store [:graph :ns-path])
       ns-text (string/join "." ns-path)
       files (get-in store [:collection :files])]
   (div
    {:style (merge ui/fullscreen style-file-tree)}
    (render-toolbar)
    (comp-text ns-text nil)
    (div
     {:style ui/row}
     (div
      {:style ui/row}
      (let [ns-names (keys files)
            segments (->> ns-names (map (fn [x] (string/split x "."))))
            file-tree (segments->tree segments)]
        (loop [children (list), path ns-path]
          (let [dict (get-in file-tree path)
                next-children (cons
                               [(string/join "/" path)
                                (if (map? dict)
                                  (div
                                   {:style style-column}
                                   (->> dict
                                        (map
                                         (fn [entry]
                                           (let [ns-piece (key entry)]
                                             [ns-piece
                                              (div
                                               {:style style-file,
                                                :event {:click (on-view path ns-piece)}}
                                               (comp-text ns-piece nil))])))))
                                  nil)]
                               children)]
            (if (empty? path)
              next-children
              (recur next-children (subvec path 0 (dec (count path)))))))))
     (comp-space 16 nil)
     (if (contains? files ns-text) (comp-brief-file ns-text (get files ns-text)))))))
