package com.ayushsrawat.logit.lucene.impl;

import com.ayushsrawat.logit.lucene.LogIndexer;
import com.ayushsrawat.logit.payload.event.LogEvent;

import java.util.List;

public class FluentBitEngine implements LogIndexer {

  @Override
  public Integer index(List<LogEvent> logEvents) {
    return 0;
  }

}
