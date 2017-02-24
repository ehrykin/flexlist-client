(ns flexlist-client.views.create-list-structure-view
    (:require [re-frame.core :as re-frame]
              [re-com.core   :as re-com]
              [reagent.core  :as reagent]
              [re-frame-datatable.core :as dt]
              [ajax.core :as ajax]
              [day8.re-frame.http-fx]))

;;-------------------subs------------------
(re-frame/reg-sub
 :active-list-id
 (fn [db _]
    (:active-list-id db)
  )
)

(re-frame/reg-sub
 :active-list-name
 (fn [db _]
   (let [active-list-id (:active-list-id db)]
      (:label (get (:lists db) active-list-id))
   )
  )
)

(re-frame/reg-sub
 :active-list-colls
 (fn [db _]
   (let [active-list-id (:active-list-id db)]
     (js/alert (:colls (get (:lists db) active-list-id)))
      (:colls (get (:lists db) active-list-id))
    )
  )
)

(re-frame/reg-sub
 :active-list-data
 (fn [db _]
   (let [active-list-id (:active-list-id db)]
      (:data (get (:lists db) active-list-id))
   )
  )
)
;;-------------------handlers------------------

(re-frame/reg-event-fx
 :add-field-to-active-list
 (fn [db [_ info]]
    {:db   (assoc db :show-twirly true)   ;; causes the twirly-waiting-dialog to show
     :http-xhrio {:method          :post
                  :uri             "/add-field-to-list"
                  :params          {:list-id        (:list-id info)
                                    :field-type-id  (:field-type-id info)
                                    :field-name     (:field-name info)}
                  :timeout         5000
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:after-add-field-to-active-list-success info]
                  :on-failure      [:after-add-field-to-active-list-failure info]}}
   )
  )

(re-frame/reg-event-db
 :after-add-field-to-active-list-success
 (fn [db [_ info result]]
   (:db (assoc-in db [:db :lists :colls] result)) ;;TODO test with real server api
  )
)

(re-frame/reg-event-db ;; TODO: replace with real failure handler
 :after-add-field-to-active-list-failure
  (fn [db [_ info result]]
    (let [cur-colls (:colls (get (-> db :db :lists) (:list-id info)))
          cur-colls-count (count cur-colls)]
    (:db (assoc-in db [:db :lists (:list-id info) :colls cur-colls-count]
                   {::dt/column-key [(keyword (:field-name info))]     ::dt/column-label   (:field-name info)}))
  ))
)

;;-------------------views---------------------

(defn add-field-panel [list-name]
  (let [active-list-id   @(re-frame/subscribe [:active-list-id])
        field-type-id     :string]
    [re-com/v-box
     :size "30%"
     :align-self :center
     :margin      "5%"
     :style {:backgroundColor "blue"}
     :children [
                [re-com/button
                 :label    "Add field"
                 :on-click  #(re-frame/dispatch [:add-field-to-active-list {:list-id        active-list-id
                                                                            :field-type-id  field-type-id
                                                                            :field-name     (str "test-field" (int (rand 100)))}])]
                 ]
     ])
  )

(defn list-grid []
  [dt/datatable
   :list-datatable
   [:active-list-data]
   @(re-frame/subscribe [:active-list-colls])
   {::dt/pagination    {::dt/enabled? true}
    ::dt/table-classes ["ui" "table" "celled"]}])

(comment (defn modal-dialog-for-add-data
  []
  (let [show? (reagent/atom false)]
    (fn []
      (let [list-colls         @(re-frame/subscribe [:active-list-colls])
            input-vals-vec     (reduce #( ( %) ) {} list-colls)]
      [re-com/v-box
       :children [[re-com/button
                   :label    "Add data to grid"
                   :class    "btn-info"
                   :on-click #(reset! show? true)]
                  (when @show?
                    [re-com/modal-panel
                     :backdrop-on-click #(reset! show? false)
                     :child [re-com/v-box
                             :width    "300px"
                             :children (reduce conj [
                                                      [re-com/title :level :level2 :label "Add data"]
                                                      [re-com/gap :size "20px"]
                                                     ]

                                                      (map #([re-com/input-text :model ]) list-colls))
                             ]
                     ]
                    )
                  ]
       ]
       )
      )
    )
)
)

(defn create-list-structure-panel [list-type-id]
  [re-com/v-box
     :size "auto"
     :align-self :center
     :margin      "5%"
     ;;:style {:backgroundColor "red"}
     :children [
                [re-com/title
                     :label @(re-frame/subscribe [:active-list-name])
                     :level :level1]

                 [re-com/h-box
                  :children [

                     [list-grid]
                     [add-field-panel] ]
                 ]

                 [re-com/button
                  :label    "Add data"
                  ];;:on-click  #(re-frame/dispatch [:open])]
                ]
  ]
)
