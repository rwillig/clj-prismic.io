(ns io.prismic.api
  (:require 
    [io.prismic.config :as cfg]
    [clj-http.client :as client]))

(def ^:dynamic *debug*      false)

(defmacro with-debug [& body]
 `(binding  [*debug* true]  ~@body))


(defn get-api 
  ([]
   (get-api cfg/prismic-url cfg/prismic-token))
  ([url]
   (get-api url nil))
  ([url token]
   (let [params       {:accept :json :as :json}
         params       (if 
                        token
                        (merge params {:query-params {:access_token token}})
                        params)
         resp         (client/get url params)]
     (-> resp :body))))

(defn get-refs [api]
  (-> api :refs))

(defn get-master-ref [api]
  (first (filter :isMasterRef (-> api :refs))))

(defn get-forms [api]
  (into [] (-> api :forms keys)))

(defn get-by-form [api form]
  (let [params    {:accept :json :as :json :debug *debug*}
        prismic   (into {} 
                          (filter  
                            (fn  [x] (identity  (second x)))  
                            (map  
                              (juxt key #(-> % val :default)) 
                              (:fields form))))
        params    (merge params {:query-params prismic})
        url       (:action form)
        resp      (client/get url params)]
    (-> resp :body)))

(defn get-form 
  ([api form]
   (let [a-ref    (:ref (get-master-ref api))]
   (get-form api form a-ref)))
  ([api form a-ref]
    (let [form      (-> api :forms form)
          form      (if 
                      a-ref
                      (assoc-in form [:fields :ref :default] a-ref)
                      form)]
      (get-by-form api form))))

(defn get-whole-form
 ([api form]
  (let [a-ref     (:ref (get-master-ref api))]
    (get-whole-form api form a-ref)))
  ([api form a-ref]
   (let [token    (get-in api [:forms form :fields :access_token :default])
         params   {:aceept :json :as :json}
         params   (if 
                    token
                    (merge params {:query-params {:access_token token}})
                    params)
         f        (get-form api form a-ref)
         rs       (:results f)
         n        (:next_page f)]
     (loop [nxt n rslts rs]
       (if (nil? nxt)
         rslts
         (let [f   (:body (client/get nxt params))
               rs  (into [] (concat rslts (:results f)))
               n   (:next_page f)]
            (recur n rs)))))))

(defn get-by-id 
  ([api id]
   (let [a-ref     (:ref (get-master-ref api))]
     (get-by-id api id a-ref)))
  ([api id a-ref]
   (let [form     (-> api :forms :everything)
         form     (if
                    a-ref
                    (assoc-in form [:fields :ref :default] a-ref)
                    form)
         q        (str  "[[:d = at(document.id, \"" id  "\")]]")
         form     (assoc-in form [:fields :q :default] q)]
     (-> (get-by-form api form) :results first))))
