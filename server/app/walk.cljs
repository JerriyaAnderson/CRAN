
(ns app.walk
  (:require ["fs" :as fs]
            ["path" :as path]))

(defn dir? [x] (.isDirectory (fs/statSync x)))
(defn file? [x] (.isFile (fs/statSync x)))

(defn walk [base-dir collect!]
  (let [children (js->clj (fs/readdirSync base-dir))]
    (doall
      (map
        (fn [child]
          (let [child-path (path/join base-dir child)]
            (cond
              (dir? child-path)
                (walk child-path collect!)
              (file? child-path)
                (collect! child-path)
              :else (println "Invalid file stats:" child-path))))
        children))))
