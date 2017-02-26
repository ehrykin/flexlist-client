(ns flexlist-client.views.create-list-manually-or-by-csv-view
    (:require [re-frame.core :as re-frame]
              [re-com.core   :as re-com]
              [reagent.core  :as reagent]
              [ajax.core :as ajax]
              [day8.re-frame.http-fx]
              [re-frame-datatable.core :as dt]
              [flexlist-client.views.file-to-table-view :as file-to-table-view]))

;;-------------------subs------------------

;;-------------------handlers------------------

(re-frame/reg-event-fx
 :create-list
 (fn [db [_ info]]
    {:db           (assoc db :show-twirly true)   ;; causes the twirly-waiting-dialog to show
     :http-xhrio  {:method          :post
                   :uri             "/create-list"
                   :params          info
                   :timeout         5000
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:after-create-list-success info]
                   :on-failure      [:after-create-list-failure info]
                   }
     }
 )
)

(re-frame/reg-event-db
 :after-create-list-success
 (fn [db [_ info result]] ;;TODO test
   (let [list-type-id  (:list-type-id  info)
         list-name     (:list-name     info)]
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
 :after-create-list-failure
  (fn [db [_ info result]]
    (let [test-list-id  (rand-int 1000)  ;;for prototype only
          list-type-id  (:list-type-id  info)
          list-name     (:list-name     info)]
      (:db
        (-> db
          (assoc-in [:db :lists test-list-id] {:label list-name
                                               :colls [{::dt/column-key [:index]     ::dt/column-label "Index"}]})
          (assoc-in [:db :active-list-id] test-list-id)
          (assoc-in [:db :active-panel] :create-list-structure-panel)
        )
      )
    )
  )
)

;;-------------------views---------------------

(def list-types [{:id 0 :label "Other (create from scratch)"}
                 {:id 2 :label "Issue/Bug Tracking"}
                 {:id 3 :label "Projects (GTD like)"}
                 {:id 4 :label "Addresses"}
                 {:id 5 :label "Tasks / Todo"}
                 {:id 6 :label "Wishes"}
                 {:id 7 :label "Inventory"}
                 {:id 8 :label "Literature"}
                 {:id 9 :label "Glossary"}
                 ])

(defn create-list-panel []
  (let [list-name (reagent/atom "")
        list-type-id (reagent/atom nil)]
    [re-com/v-box
     :size "auto"
     :align-self :center
     :margin      "5%"
     :children [
                 [re-com/title
                   :label "List name"
                   :level :level3]
                [re-com/input-text
                 :placeholder "MyList"
                 :model        list-name
                 :on-change   #(reset! list-name %)]

                 [re-com/title
                   :label "List content"
                   :level :level3]
                [re-com/single-dropdown
                 :choices list-types
                 :model   list-type-id
                 :width    "100%"
                 :on-change   #(reset! list-type-id %)]

                 [re-com/gap :size "10px"]

                [re-com/button
                 :label    "Create now!"
                 :on-click  #(re-frame/dispatch [:create-list {:list-type-id   @list-type-id
                                                               :list-name      @list-name}])]

                 ]
     ])
)

(defn create-list-manually-or-by-csv-panel []
  [re-com/v-box
     :size "auto"
     :align-self :center
     :margin      "5%"
     :children [
                 [re-com/title
                   :label "Create list manually or by csv-file"
                   :level :level1]
                 [re-com/h-box
                    :children [
                               [create-list-panel]
                               [file-to-table-view/file-to-table-panel]
                              ]
                 ]
    ]
 ]
)
