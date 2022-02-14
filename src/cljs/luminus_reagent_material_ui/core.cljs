(ns luminus-reagent-material-ui.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   [markdown.core :refer [md->html]]
   [luminus-reagent-material-ui.ajax :as ajax]
   [ajax.core :refer [GET POST]]
   [reitit.core :as reitit]
   [clojure.string :as string]
   [reagent-mui.x.data-grid :refer [data-grid]]
   [reagent-mui.util :refer [wrap-clj-function]])
  (:import goog.History))

(defonce session (r/atom {:page :home}))

(def columns [{:field      :id
               :headerName "ID"
               :width      90}
              {:field      :first-name
               :headerName "First name"
               :width      150
               :editable   true}
              {:field      :last-name
               :headerName "Last name"
               :width      150
               :editable   true}
              {:field      :age
               :headerName "Age"
               :type       :number
               :width      110
               :editable   true}
              {:field       :full-name
               :headerName  "Full name"
               :description "This column has a value getter and is not sortable."
               :sortable    false
               :width       160
               :valueGetter (wrap-clj-function
                             (fn [params]
                               (str (get-in params [:row :first-name] "") " " (get-in params [:row :last-name] ""))))}])

(def rows [{:id 1 :last-name "Snow" :first-name "Jon" :age 35}
           {:id 2 :last-name "Lannister" :first-name "Cersei" :age 42}
           {:id 3 :last-name "Lannister" :first-name "Jaime" :age 45}
           {:id 4 :last-name "Stark" :first-name "Arya" :age 16}
           {:id 5 :last-name "Targaryen" :first-name "Daenerys" :age nil}
           {:id 6 :last-name "Melisandre" :first-name nil :age 150}
           {:id 7 :last-name "Clifford" :first-name "Ferrara" :age 44}
           {:id 8 :last-name "Frances" :first-name "Rossini" :age 36}
           {:id 9 :last-name "Roxie" :first-name "Harvey" :age 65}])

(defn component []
  [:div {:style {:height 400 :width 800}}
   [data-grid {:rows                       rows
               :columns                    columns
               :page-size                  5
               :rows-per-page-options      [5]
               :checkbox-selection         true
               :disable-selection-on-click true}]])

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :class (when (= page (:page @session)) "is-active")}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
    [:nav.navbar.is-info>div.container
     [:div.navbar-brand
      [:a.navbar-item {:href "/" :style {:font-weight :bold}} "luminus_reagent_material_ui"]
      [:span.navbar-burger.burger
       {:data-target :nav-menu
        :on-click #(swap! expanded? not)
        :class (when @expanded? :is-active)}
       [:span] [:span] [:span]]]
     [:div#nav-menu.navbar-menu
      {:class (when @expanded? :is-active)}
      [:div.navbar-start
       [nav-link "#/" "Home" :home]
       [nav-link "#/about" "About" :about]]]]))

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]])


(defn home-page []
  [:section.section>div.container>div.content
   (when-let [docs (:docs @session)]
     [:div {:dangerouslySetInnerHTML {:__html (md->html docs)}}])])

(def pages
  {:home #'home-page
   :about #'about-page})

(defn page []
  [(pages (:page @session))])

;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/" :home]
    ["/about" :about]]))

(defn match-route [uri]
  (->> (or (not-empty (string/replace uri #"^.*#" "")) "/")
       (reitit/match-by-path router)
       :data
       :name))
;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     HistoryEventType/NAVIGATE
     (fn [^js/Event.token event]
       (swap! session assoc :page (match-route (.-token event)))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(swap! session assoc :docs %)}))

(defn ^:dev/after-load mount-components []
  (rdom/render [#'navbar] (.getElementById js/document "navbar"))
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (ajax/load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
