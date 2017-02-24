(ns flexlist-client.events
    (:require [re-frame.core :as re-frame]
              [flexlist-client.db :as db]
              [ajax.core :as ajax]
              [day8.re-frame.http-fx]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))


(re-frame/reg-event-db
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))


(re-frame/reg-event-db
 :set-active-list
 (fn [db [_ list-id]]
   (assoc
     (assoc db :active-list-id list-id)
      :active-panel :create-list-structure-panel
   )
  )
)
