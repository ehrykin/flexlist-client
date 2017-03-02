(ns flexlist-client.views.file-to-table-view
    (:require [re-frame.core :as re-frame]
              [re-com.core   :as re-com]
              [reagent.core  :as reagent]
              [re-frame-datatable.core :as dt]
              [ajax.core :as ajax]
              [day8.re-frame.http-fx]
              [goog.labs.format.csv :as csv]))

;;-------------------handlers------------------

(re-frame/reg-event-fx
 :create-list-from-csv
 (fn [db [_ info]]
    {:db           (assoc db :show-twirly true)   ;; causes the twirly-waiting-dialog to show
     :http-xhrio  {:method          :post
                   :uri             "/create-list-from-csv"
                   :params          info
                   :timeout         5000
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:after-create-list-from-csv-success info]
                   :on-failure      [:after-create-list-from-csv-failure info]
                   }
     }
 )
)

(re-frame/reg-event-db
 :after-create-list-from-csv-success
 (fn [db [_ info result]] ;;TODO test
   (let [list-type-id              (:list-type-id  info)
         list-name                 (:list-name     info)
         coll-names                (:coll-names    info)
         coll-data-object-vectors  (:coll-data-object-vectors info)]
      (:db
        (-> db
          (assoc-in [:db :lists {:list-id result}] {:label list-name
                                                    :cols [{::dt/column-key [:index]   ::dt/column-label "Index"}]})
          (assoc-in [:db :active-list-id] {:list-id result})
          (assoc-in [:db :active-panel] :create-list-structure-panel)
        )
      )
   )
  )
)

(re-frame/reg-event-db ;; TODO: replace with real failure handler
 :after-create-list-from-csv-failure
  (fn [db [_ info result]]
    (let [test-list-id              (rand-int 1000)  ;;for prototype only
          list-type-id              (:list-type-id  info)
          list-name                 (:list-name     info)
          coll-names                (:coll-names    info)
          coll-data-objects-vector  (:coll-data-object-vectors info)
          colls (vec (map (fn [coll-name] {::dt/column-key [(keyword coll-name)]  ::dt/column-label coll-name}) coll-names))]
      (:db
        (-> db
          (assoc-in [:db :lists test-list-id] {:label list-name
                                               :colls colls
                                               :data  coll-data-objects-vector})
          (assoc-in [:db :active-list-id] test-list-id)
          (assoc-in [:db :active-panel] :create-list-structure-panel)
        )
      )
    )
  )
)


;;-------------------views---------------------


(defn file-to-table-panel []
    [re-com/v-box
     :size "auto"
     :align-self :center
     :margin      "5%"
     :children [
                [:input
                 {
                  :type "file"
                  :on-change (fn [file]
                               (let [file       (-> file .-target .-files (.item 0))
                                     file-name  (.-name file)
                                     reader     (new js/FileReader)
                                     list-type-id     0]
                                 (set! (.-onload reader) (fn[]
                                                           (let [csv-data                   (-> reader .-result (csv/parse true ",") js->clj)
                                                                 coll-names                 (get csv-data 0)
                                                                 coll-data-vectors          (doall (vec (rest csv-data) ))
                                                                 coll-name-symbols          (vec (map #(keyword %) coll-names))
                                                                 coll-data-object-vectors   (vec (map #(zipmap coll-name-symbols %) coll-data-vectors))
                                                                 active-list-id            @(re-frame/subscribe [:active-list-id])
                                                                 ]
                                                             (re-frame/dispatch [:create-list-from-csv {:list-type-id              list-type-id
                                                                                                        :list-name                 file-name
                                                                                                        :coll-names                coll-names
                                                                                                        :coll-data-object-vectors  coll-data-object-vectors}])
                                                           )
                                                         )
                                 )
                                 (.readAsText reader file)
                              )
                             )
                  }
                ]
               ]
     ]
)
