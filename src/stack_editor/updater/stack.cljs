
(ns stack-editor.updater.stack
  (:require [clojure.string :as string]
            [stack-editor.util.analyze :refer [locate-ns compute-ns]]
            [stack-editor.util.detect :refer [strip-atom contains-def?]]
            [stack-editor.util :refer [remove-idx]]))

(defn collapse [store op-data op-id]
  (let [cursor op-data]
    (update
     store
     :writer
     (fn [writer]
       (-> writer (assoc :pointer 0) (update :stack (fn [stack] (subvec stack cursor))))))))

(defn helper-put-path [ns-part name-part]
  (fn [writer]
    (-> writer
        (update
         :stack
         (fn [stack]
           (let [next-pointer (inc (:pointer writer)), code-path [ns-part :defs name-part]]
             (if (< (dec (count stack)) next-pointer)
               (conj stack code-path)
               (if (= code-path (get stack next-pointer))
                 stack
                 (conj (into [] (subvec stack 0 next-pointer)) code-path))))))
        (update :pointer inc)
        (assoc :focus []))))

(defn helper-notify [op-id data]
  (fn [notifications] (into [] (cons [op-id data] notifications))))

(defn helper-create-def [ns-part name-part code-path focus]
  (fn [files]
    (if (contains-def? files ns-part name-part)
      files
      (assoc-in
       files
       [ns-part :defs name-part]
       (let [as-fn? (and (not (empty? focus)) (zero? (last focus)))]
         (if as-fn?
           (let [expression (get-in files (concat code-path (butlast focus)))]
             (if (> (count expression) 1)
               ["defn" name-part (subvec expression 1)]
               ["defn" name-part []]))
           ["def" name-part]))))))

(defn goto-definition [store op-data op-id]
  (let [forced? op-data
        writer (:writer store)
        pointer (:pointer writer)
        focus (:focus writer)
        stack (:stack writer)
        pointer (:pointer writer)
        pkg (get-in store [:collection :package])
        files (get-in store [:collection :files])
        code-path (get stack pointer)
        [current-ns kind extra-name] code-path
        drop-pkg (fn [x] (if (string? x) (string/replace x (str pkg ".") "") x))]
    (let [target (get-in store (concat [:collection :files] code-path focus))]
      (if (string? target)
        (let [stripped-target (strip-atom target)]
          (if forced?
            (if (string/includes? stripped-target "/")
              (let [[ns-part var-part] (string/split stripped-target "/")
                    that-ns (drop-pkg (locate-ns ns-part current-ns files))]
                (if (contains? files that-ns)
                  (if (contains-def? files that-ns var-part)
                    (update store :writer (helper-put-path that-ns var-part))
                    (-> store
                        (update-in
                         [:collection :files]
                         (helper-create-def that-ns var-part code-path focus))
                        (update :writer (helper-put-path that-ns var-part))))
                  (-> store
                      (update
                       :notifications
                       (helper-notify op-id (str "foreign namespace: " that-ns))))))
              (let [ns-part (compute-ns stripped-target current-ns files)
                    that-ns (if (some? ns-part) (drop-pkg ns-part) current-ns)]
                (println "forced piece:" that-ns stripped-target)
                (if (contains? files that-ns)
                  (-> store
                      (update-in
                       [:collection :files]
                       (helper-create-def that-ns stripped-target code-path focus))
                      (update :writer (helper-put-path that-ns stripped-target)))
                  (-> store
                      (update
                       :notifications
                       (helper-notify op-id (str "foreign namespace: " that-ns)))))))
            (let [that-ns (drop-pkg (compute-ns stripped-target current-ns files))
                  var-part (last (string/split stripped-target "/"))]
              (println "Search result:" that-ns var-part current-ns)
              (if (contains-def? files that-ns var-part)
                (if (= [that-ns :defs var-part] code-path)
                  store
                  (update store :writer (helper-put-path that-ns var-part)))
                (-> store
                    (update
                     :notifications
                     (helper-notify op-id (str "no namespace for: " stripped-target))))))))
        store))))

(defn go-next [store op-data]
  (-> store
      (update
       :writer
       (fn [writer]
         (if (< (:pointer writer) (dec (count (:stack writer))))
           (-> writer (update :pointer inc) (assoc :focus []))
           writer)))))

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
            (fn [pointer]
              (if (= pointer (dec (count (:stack writer)))) (dec pointer) pointer))))))))
