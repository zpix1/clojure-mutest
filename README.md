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

### Setting mutators to run

Config entry `:mutators` can be set to "all" to run all the mutators.  
If you want to run only a subset of mutators, set `:mutators` to list of their names.  
Example:
```edn
{:mutators ["and-or", "gt-gte"]
...
```

The following mutators are currently available:
```
and-or
gt-gte
lt-lte
true-false
plus-mul
swap-zero
eq-noteq
empty?-seq
not-boolean
replace-if-with-then
```

## Usage

Leiningen is required for this library.

Use `lein run` to run tests and get HTML report.
