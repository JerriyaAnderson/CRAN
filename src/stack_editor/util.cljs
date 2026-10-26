
(ns stack-editor.util )

(defn remove-idx [xs idx]
  (let [xs-size (count xs)]
    (cond
      (or (>= idx xs-size) (neg? idx)) xs
      (= xs-size 1) []
      (zero? idx) (subvec xs 1)
      (= idx (dec xs-size)) (subvec xs 0 idx)
      :else (into [] (concat (subvec xs 0 idx) (subvec xs (inc idx)))))))

(defn now! [] (.now js/performance))

(defn helper-notify [op-id data]
  (fn [notifications] (into [] (cons [op-id data] notifications))))

(defn helper-put-ns [ns-name]
  (fn [writer]
    (-> writer
        (update :pointer inc)
        (assoc :focus [])
        (update
         :stack
         (fn [stack] (conj (subvec stack 0 (inc (:pointer writer))) [ns-name :ns]))))))
