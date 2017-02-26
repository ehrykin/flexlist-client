(ns flexlist-client.views.login-view
    (:require [re-frame.core :as re-frame]
              [re-com.core   :as re-com]
              [re-com.misc   :as re-com-misc]
              [reagent.core  :as reagent]
              [ajax.core :as ajax]
              [day8.re-frame.http-fx]))

;;-----------------handlers---------------------

(re-frame/reg-event-fx
 :login
 (fn [db [_ info]]
   (let [username  (:username  info)]
    {:db (assoc db :show-twirly true)   ;; causes the twirly-waiting-dialog to show
     :http-xhrio {:method          :post
                  :uri             "/login"
                  :params          info
                  :timeout         5000
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:after-login-success username]
                  :on-failure      [:after-login-failure username]}}
   )
  )
)

(re-frame/reg-event-db
 :after-login-success
 (fn [db [_ username result]]
   (:db (assoc-in db [:db :userid] (:userid result)))
  )
)

(re-frame/reg-event-db ;; TODO: replace with failure handler
 :after-login-failure
  (fn [db [_ username result]]
   (:db (-> db
            (assoc-in [:db :userid] username)
            (assoc-in [:db :active-panel] :create-list-manually-or-by-csv-panel)
        )
   )
  )
)

;;------------------views--------------------

(defn login-panel []
  (let [username (reagent/atom "")
        password (reagent/atom "")]
    [re-com/v-box
     :size "auto"
     :align-self :baseline
     :margin      "5%"
     :children [
                 [re-com/title
                   :label "Login"
                   :level :level1]

                 [re-com/title
                   :label "Username:"
                   :level :level3]
                [re-com/input-text
                 :model        username
                 :on-change   #(reset! username %)]

                 [re-com/title
                   :label "Password:"
                   :level :level3]
                [re-com/input-text
                 :model        password
                 :on-change   #(reset! password %)]

                 [re-com/gap :size "10px"]

                [re-com/button
                 :label    "Login"
                 :on-click  #(re-frame/dispatch [:login {:username @username
                                                         :password @password
                                                         }])]

                 ]
     ])
  )
