(ns flexlist-client.views.user-lists-view
    (:require [re-frame.core :as re-frame]
              [re-com.core   :as re-com]
              [re-com.misc   :as re-com-misc]
              [reagent.core  :as reagent]
              [ajax.core :as ajax]
              [day8.re-frame.http-fx]))

;;-------------------subs------------------

(re-frame/reg-sub
 :user-lists
 (fn [db _]
   (let [list-ids (-> db :lists keys)]
      (vec (map (fn [list-id]
                  {:id list-id :label (-> db :lists (get list-id) :label)} ;;(:label (get (:lists db) list-id))}
                )  list-ids))
    )
  )
)

;;------------------views--------------------

(defn user-lists-panel []
  (let [user-lists   @(re-frame/subscribe [:user-lists])
        selected-list-id (reagent/atom nil)]
    [re-com/v-box
     :size "auto"
     :width "20%"
     :align-self  :center
     :align       :center
     :margin      "5%"
     :children [

                  [re-com/title
                   :label "Your lists:"
                   :level :level2]
                  [re-com/single-dropdown
                   :choices user-lists
                   :model   selected-list-id
                   :width    "100%"
                   :on-change   #(reset! selected-list-id %)]

                  [re-com/gap :size "10px"]

                  [re-com/button
                   :label    "Open"
                   :on-click  #(re-frame/dispatch [:set-active-list @selected-list-id])
                   ]

                 ]
     ])
  )
