(set-env!
  :resource-paths #{"src"}
  :dependencies '[[org.clojure/data.json  "0.2.5"]
                  [cheshire  "5.4.0"]
                  [clj-time "0.9.0"]
                  [slingshot  "0.12.1"]
                  [mvxcvi/puget "0.8.0"]
                  [clj-http "1.0.1"]])


(task-options!
  pom {:project 'prismic.io
       :version "0.1.0"
       :description "clojure wrapper for prismic.io REST API"})



