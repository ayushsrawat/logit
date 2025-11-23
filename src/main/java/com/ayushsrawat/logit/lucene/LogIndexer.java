package com.ayushsrawat.logit.lucene;

import com.ayushsrawat.logit.payload.event.LogEvent;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;

import java.util.List;

/**
 * Indexes the LogEvents
 */
public interface LogIndexer<T extends LogEvent> {

  Integer index(String indexName, List<T> logEvents);

  /// INTERNAL

  IndexWriter getIndexWriter(String index);

  Analyzer getIndexAnalyzer();

  List<Document> eventsToDocument(List<T> logEvents);

}
