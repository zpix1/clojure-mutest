(defproject clojure-mutest "0.1.0-SNAPSHOT"
  :description "Clojure mutation testing framework"
  :url "https://github.com/zpix1/clojure-mutest/tree/main/src/clojure_mutest"
  :main "clojure-mutest.core"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [rewrite-clj/rewrite-clj "1.1.47"]
                 [hiccup "2.0.0-RC2"]]
  :uberjar {:aot :all}
  :repl-options {:init-ns clojure-mutest.core})
