#!/usr/bin/env zsh

if [ ! -d ./state ]; then
  mkdir ./state
fi

fluent-bit -c /Users/ayushrawat/cs/java/logit/fluentbit/fluent-bit.yaml
