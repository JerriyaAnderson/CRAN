
(ns app.updater.stack
  (:require [clojure.string :as string]
            [app.util.analyze :refer [list-dependent-ns parse-ns-deps extract-deps]]
            [app.util.detect :refer [strip-atom contains-def? =path?]]
            [app.util :refer [remove-idx helper-notify helper-create-def make-path has-ns?]]
            (app.util.stack :refer (push-path push-paths))
            (clojure.set :refer (union))))

(defn collapse [store op-data op-id]
  (let [cursor op-data]
    (update
     store
     :writer
     (fn [writer]
       (-> writer (assoc :pointer 0) (update :stack (fn [stack] (subvec stack cursor))))))))

(defn go-next [store op-data]
  (-> store
      (update
       :writer
       (fn [writer]
         (if (< (:pointer writer) (dec (count (:stack writer))))
           (-> writer (update :pointer inc) (assoc :focus []))
           writer)))))

(defn dependents [store op-data op-id]
  (let [writer (:writer store)
        {stack :stack, pointer :pointer} writer
        code-path (get stack pointer)
        {ns-part :ns, kind :kind, extra-name :extra} code-path
        pkg (get-in store [:collection :package])
        def-as-dep {:ns (str pkg "." ns-part), :def extra-name, :external? false}
        files (get-in store [:collection :files])
        ns-list (list-dependent-ns ns-part files pkg)]
    (case kind
      :defs
        (let [new-paths (->> (conj ns-list ns-part)
                             (map
                              (fn [ns-text]
                                (let [file (get files ns-text)]
                                  (into
                                   #{}
                                   (concat
                                    (->> (:defs file)
                                         (filter
                                          (fn [entry]
                                            (let [def-deps (extract-deps
                                                            (subvec (val entry) 2)
                                                            ns-part
                                                            file
                                                            pkg)]
                                              (contains? def-deps def-as-dep))))
                                         (map
                                          (fn [entry]
                                            {:kind :defs,
                                             :ns ns-text,
                                             :extra (first entry),
                                             :focus [2]})))
                                    (let [proc-deps (extract-deps
                                                     (:procs file)
                                                     ns-part
                                                     file
                                                     pkg)]
                                      (if (contains? proc-deps def-as-dep)
                                        (list
                                         {:kind :procs, :ns ns-text, :extra nil, :focus [0]})
                                        (list))))))))
                             (apply concat)
                             (filter (fn [x] (not (=path? x code-path)))))]
          (if (empty? new-paths)
            (update store :notifications (helper-notify op-id "Nothing found."))
            (update store :writer (push-paths new-paths))))
      :ns
        (let [new-paths (map (fn [x] [x :ns]) ns-list)]
          (comment println pointer new-paths)
          (update store :writer (push-paths new-paths)))
      store)))

(defn point-to [store op-data op-id]
  (let [pointer op-data] (assoc-in store [:writer :pointer] pointer)))

(defn go-back [store op-data]
  (-> store
      (update
       :writer
       (fn [writer]
         (if (pos? (:pointer writer))
           (-> writer (update :pointer dec) (assoc :focus []))
           writer)))))

(defn shift-one [store op-data op-id]
  (let [pointer op-data]
    (update
     store
     :writer
     (fn [writer]
       (-> writer
           (update :stack (fn [stack] (remove-idx stack pointer)))
           (update
            :pointer
            (fn [p] (if (= p pointer) (if (pos? p) (dec p) p) (if (< p pointer) p (dec p))))))))))

(defn goto-definition [store op-data op-id]
  (let [forced? op-data
        {pkg :package, files :files} (get-in store [:collection])
        pkg_ (str pkg ".")
        {stack :stack, pointer :pointer} (:writer store)
        code-path (get stack pointer)
        focus (:focus code-path)
        target (strip-atom (get-in store (concat (make-path code-path) focus)))
        ns-deps (parse-ns-deps (get-in files [(:ns code-path) :ns]))
        current-ns-defs (get-in files [(:ns code-path) :defs])
        dep-info (if (has-ns? target)
                   (let [[ns-text def-text] (string/split target "/")
                         maybe-info (get ns-deps ns-text)]
                     (if (and (some? maybe-info) (= :as (:kind maybe-info)))
                       {:ns (:ns maybe-info), :def def-text}
                       nil))
                   (let [maybe-info (get ns-deps target)]
                     (if (and (some? maybe-info) (= :refer (:kind maybe-info)))
                       {:ns (:ns maybe-info), :def target}
                       (if (or (contains? current-ns-defs target) forced?)
                         {:ns (str pkg_ (:ns code-path)), :def target}
                         nil
                         ))))]
    (comment println target dep-info)
    (if (some? dep-info)
      (if (string/starts-with? (:ns dep-info) pkg_)
        (let [existed? (some? (get-in files [(:ns dep-info) :defs (:def dep-info)]))
              shorten-ns (string/replace-first (:ns dep-info) pkg_ "")
              touch-def (fn [store]
                          (println "touching" existed?)
                          (if existed?
                            store
                            (-> store
                                (update-in
                                 [:collection :files]
                                 (helper-create-def
                                  shorten-ns
                                  (:def dep-info)
                                  code-path
                                  (:focus code-path))))))]
          (-> store
              (touch-def)
              (update
               :writer
               (push-path {:kind :defs, :ns shorten-ns, :extra (:def dep-info), :focus [2]}))))
        (-> store
            (update
             :notifications
             (helper-notify op-id (str "External package: " (:ns dep-info))))))
      (-> store (update :notifications (helper-notify op-id (str "Can't find: " target)))))))
