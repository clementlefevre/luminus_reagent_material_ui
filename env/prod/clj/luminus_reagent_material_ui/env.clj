(ns luminus-reagent-material-ui.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[luminus_reagent_material_ui started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[luminus_reagent_material_ui has shut down successfully]=-"))
   :middleware identity})
