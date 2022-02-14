(ns luminus-reagent-material-ui.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [luminus-reagent-material-ui.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[luminus_reagent_material_ui started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[luminus_reagent_material_ui has shut down successfully]=-"))
   :middleware wrap-dev})
