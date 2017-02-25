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

(re-frame/reg-sub
 :show-add-grid-data-dialog?
 (fn [db _]
   (:show-add-grid-data-dialog? db)
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

(re-frame/reg-event-db
 :open-dialog-for-grid-data-addition
  (fn [db _]
   (assoc db :show-add-grid-data-dialog? true)
  )
)

(re-frame/reg-event-db
 :close-dialog-for-grid-data-addition
  (fn [db _]
   (assoc db :show-add-grid-data-dialog? false)
  )
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

(defn merge-list-coll-info-to-colls-info-object [obj list-coll]
  (let [col-name    (get (::dt/column-key list-coll) 0)
        col-label   (get (::dt/column-key list-coll) 0)]
    (assoc obj col-name {:label   col-label
                         :type    :string
                         :value   (reagent/atom "")}) ;;TODO make other data types support
  )
)

(defn map-colls-info-object-to-widget [coll-info-object]
  (let [coll-value  (:value coll-info-object)
        coll-label  (:label coll-info-object)]
    [
      (when (not (nil? coll-label)) [re-com/title :label coll-label :level :level2])
      [re-com/input-text
        :model       coll-value
        :on-change   #(reset! coll-value %)
      ]
    ]
  )
)

(comment (defn list-colls-to-widgets [list-colls]
 (let [colls-info-object (reduce merge-list-coll-info-to-colls-info-object {} list-colls)
       widgets           (mapcat map-colls-info-object-to-widget (vals colls-info-object))
       ]
   (vec widgets)
 )
))

(defn modal-dialog-for-add-data
  []
    (fn []
      (let [list-colls @(re-frame/subscribe [:active-list-colls])
            colls-info-object (reduce merge-list-coll-info-to-colls-info-object {} list-colls)
            input-vals-vec (vec (mapcat map-colls-info-object-to-widget (vals colls-info-object))) ;;(list-colls-to-widgets @(re-frame/subscribe [:active-list-colls])))
            ;;data-for-dispatch (map #({%}) colls-info-object)  ;;TODO get data to dispatch
            dialog-box-widgets (concat [[re-com/title :level :level2 :label "Add data"]]
                                        input-vals-vec
                                         [
                                           [re-com/h-box
                                            :children
                                             [
                                               [re-com/button
                                               :label "Close"
                                               :on-click  #(re-frame/dispatch [:close-dialog-for-grid-data-addition])]

                                               [re-com/button
                                               :label "Add"
                                               :on-click  #(re-frame/dispatch [:add-data-to-grid {}])] ;;TODO add data to dispatch
                                              ]
                                            ]
                                          ])
            ]
      [re-com/v-box
       :children [
                  (when @(re-frame/subscribe [:show-add-grid-data-dialog?])
                    [re-com/modal-panel
                     ;;:backdrop-on-click (re-frame/dispatch [:close-dialog-for-grid-data-addition])
                     :child [re-com/v-box
                             :width    "300px"
                             :children dialog-box-widgets
                             ]
                     ]
                    )
                  ]
       ]
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
                  :on-click  #(re-frame/dispatch [:open-dialog-for-grid-data-addition])
                 ]
                 [modal-dialog-for-add-data]
                ]
  ]
)
