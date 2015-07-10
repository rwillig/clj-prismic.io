(ns io.prismic.config)

(defonce prismic-url     (or 
                            (System/getProperty "prismic-url")
                            (System/getenv "PRISMIC_URL")))

(defonce prismic-token   (or 
                            (System/getProperty "prismic-token")
                            (System/getenv "PRISMIC_TOKEN")))
