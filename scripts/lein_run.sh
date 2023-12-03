#!/bin/sh

(unset CLASSPATH; cd $1; lein test)