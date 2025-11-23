package com.ayushsrawat.logit.lucene.impl;

import com.ayushsrawat.logit.lucene.LogIndexer;
import com.ayushsrawat.logit.payload.request.FluentBitEvent;
import com.ayushsrawat.logit.util.DateUtil;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class FluentBitEngine implements LogIndexer<FluentBitEvent> {

  @Value("${logit.index.dir}")
  private String logitIndexDir;

  private final DateUtil dateUtil;

  private final Map<String, IndexWriter> writers = new ConcurrentHashMap<>();

  public enum IndexField {
    TIMESTAMP("timestamp"),
    CLASS("class"),
    METHOD("method"),
    LEVEL("level"),
    MESSAGE("message");

    public final String name;

    IndexField(String name) {
      this.name = name;
    }
  }

  @Override
  public Integer index(String indexName, List<FluentBitEvent> logEvents) {
    IndexWriter writer = getIndexWriter(indexName);
    if (writer == null) {
      return 0;
    }

    try {
      var docs = eventsToDocument(logEvents);
      writer.addDocuments(docs);
      writer.commit();
      return docs.size();
    } catch (IOException e) {
      log.error("Error indexing documents for index {}: {}", indexName, e.getMessage());
      return 0;
    }
  }

  private IndexWriter getIndexWriter(String indexName) {
    return writers.computeIfAbsent(indexName, k -> {
      Path indexPath = Paths.get(logitIndexDir + k);
      try {
        if (!Files.exists(indexPath)) {
          Files.createDirectories(indexPath);
        }
        Directory directory = FSDirectory.open(indexPath);
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer)
            .setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        return new IndexWriter(directory, iwc);
      } catch (IOException e) {
        log.error("Error creating IndexWriter for index {}: {}", k, e.getMessage());
        return null;
      }
    });
  }

  @PreDestroy
  public void closeAll() {
    log.info("Closing all IndexWriters...");
    writers.forEach((index, writer) -> {
      try {
        writer.close();
      } catch (IOException e) {
        log.error("Error closing IndexWriter for index {}: {}", index, e.getMessage());
      }
    });
    writers.clear();
  }

  @Override
  public List<Document> eventsToDocument(List<FluentBitEvent> events) {
    List<Document> docs = new ArrayList<>();
    for (FluentBitEvent e : events) {
      if (e.isIncomplete())
        continue;
      Document doc = new Document();
      long timestamp = dateUtil.convertToLong(e.getTimestamp());
      doc.add(new LongPoint(IndexField.TIMESTAMP.name, timestamp));
      doc.add(new StoredField(IndexField.TIMESTAMP.name, timestamp));
      doc.add(new TextField(IndexField.CLASS.name, e.getClazz(), Field.Store.YES));
      doc.add(new TextField(IndexField.METHOD.name, e.getMethod(), Field.Store.YES));
      doc.add(new TextField(IndexField.LEVEL.name, e.getLevel(), Field.Store.YES));
      doc.add(new TextField(IndexField.MESSAGE.name, e.getMessage(), Field.Store.YES));
      docs.add(doc);
    }
    return docs;
  }

}
