#!/usr/bin/env zsh

STATE_DB_PATH=$HOME/cs/java/logit/fluentbit/state/
LUCENE_INDEX_PATH=$HOME/lucene/logit/
CHRONICLE_QUEUE_PATH=$HOME/chronicle

rm -f $STATE_DB_PATH*
rm -rf $CHRONICLE_QUEUE_PATH*
rm -rf $LUCENE_INDEX_PATH
