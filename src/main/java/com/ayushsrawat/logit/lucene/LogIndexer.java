package com.ayushsrawat.logit.lucene;

import com.ayushsrawat.logit.payload.event.LogEvent;

import java.util.List;

/**
 * Indexes the LogEvents
 */
public interface LogIndexer {

  Integer index(List<LogEvent> logEvents);

}
