package com.ayushsrawat.logit.lucene;

import com.ayushsrawat.logit.payload.event.LogEvent;
import org.apache.lucene.document.Document;

import java.util.List;

/**
 * Indexes the LogEvents
 */
public interface LogIndexer<T extends LogEvent> {

  Integer index(String indexName, List<T> logEvents);

  List<Document> eventsToDocument(List<T> logEvents);

}
