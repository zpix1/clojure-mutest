# clojure-mutest

A clojure mutation testing library.

## Configuration

Create a `clojure-mutest-config.edn` file at project root.

Example contents:

```edn
{:mutators "all"
 :check-tests-are-valid false
 ;; should be valid path to file or to directory
 :path "./resources/testp/src/testp"
 ;; should be a valid path to file
 :run-tests "./scripts/lein_run.sh"
 ;; argument for run-tests script
 :run-tests-arg "./resources/testp"
 ;; report path
 :output-html "./output.html"}
```

## Usage

Leiningen is required for this library.

Use `lein run` to run tests and get HTML report.
