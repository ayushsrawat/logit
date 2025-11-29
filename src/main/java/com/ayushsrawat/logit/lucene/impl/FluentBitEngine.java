package com.ayushsrawat.logit.lucene.impl;

import com.ayushsrawat.logit.lucene.LogIndexer;
import com.ayushsrawat.logit.lucene.LogSearcher;
import com.ayushsrawat.logit.lucene.SearchHit;
import com.ayushsrawat.logit.payload.request.FluentBitEvent;
import com.ayushsrawat.logit.payload.request.SearchRequest;
import com.ayushsrawat.logit.util.DateUtil;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.miscellaneous.KeywordRepeatFilter;
import org.apache.lucene.analysis.miscellaneous.RemoveDuplicatesTokenFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.CollectorManager;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorable;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.SimpleCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Bits;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class FluentBitEngine implements LogIndexer<FluentBitEvent>, LogSearcher<SearchHit<FluentBitEvent>> {

  @Value("${logit.index.dir}")
  private String logitIndexDir;

  private final DateUtil dateUtil;

  private final Map<String, IndexWriter> writers = new ConcurrentHashMap<>();
  private final Map<String, IndexSearcher> searchers = new ConcurrentHashMap<>();

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

  @Override
  public IndexWriter getIndexWriter(String indexName) {
    return writers.computeIfAbsent(indexName, index -> {
      Path indexPath = Paths.get(logitIndexDir + index);
      try {
        if (!Files.exists(indexPath)) {
          Files.createDirectories(indexPath);
        }
        Directory directory = FSDirectory.open(indexPath);
        Analyzer analyzer = getIndexAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer)
            .setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        return new IndexWriter(directory, iwc);
      } catch (IOException e) {
        log.error("Error creating IndexWriter for index {}: {}", index, e.getMessage());
        return null;
      }
    });
  }

  @Override
  public Integer docsCount(String index) {
    int count = 0;
    Path indexPath = Paths.get(logitIndexDir + index);
    try (Directory directory = FSDirectory.open(indexPath); IndexReader reader = DirectoryReader.open(directory)) {
      for (LeafReaderContext leafContext : reader.leaves()) {
        //noinspection resource
        LeafReader leafReader = leafContext.reader();
        Bits liveDocs = leafReader.getLiveDocs();
        for (int i = 0; i < leafReader.maxDoc(); i++) {
          if (liveDocs == null || liveDocs.get(i)) {
            count += 1;
          }
        }
      }
      return count;
    } catch (IOException e) {
      log.error("Error while counting total indexed docs for index {}: {}", index, e.getMessage());
    }
    return 0;
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

  @Override
  public Analyzer getIndexAnalyzer() {
    return new Analyzer() {
      @Override
      protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer source = new StandardTokenizer();
        TokenStream result = new EnglishPossessiveFilter(source);
        result = new LowerCaseFilter(result);
        result = new StopFilter(result, EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
        result = new KeywordRepeatFilter(result); // repeat tokens
        result = new SnowballFilter(result, new EnglishStemmer()); // apply stemming in 1st one of them
        result = new RemoveDuplicatesTokenFilter(result); // remove one if both are same
        return new TokenStreamComponents(source, result);
      }
    };
  }

  ///
  /// SEARCHING...
  ///

  @Override
  public Analyzer getSearchAnalyzer(boolean stem) {
    return new Analyzer() {
      @Override
      protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer source = new StandardTokenizer();
        TokenStream result = new EnglishPossessiveFilter(source);
        result = new LowerCaseFilter(result);
        result = new StopFilter(result, EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
        if (stem) {
          result = new SnowballFilter(result, new EnglishStemmer()); // stem tokens only if explicitly said
        }
        return new TokenStreamComponents(source, result);
      }
    };
  }

  @Override
  public List<SearchHit<FluentBitEvent>> search(SearchRequest searchQuery) {
    try {
      IndexSearcher searcher = getIndexSearcher(searchQuery.getIndex());
      if (searcher == null) return List.of();
      List<String> searchFields = Arrays.stream(IndexField.values())
        .map(indexField -> indexField.name)
        .filter(name -> searchQuery.getFields().contains(name))
        .toList();
      BooleanQuery.Builder bqb = new BooleanQuery.Builder();
      QueryParser parser = new MultiFieldQueryParser(searchFields.toArray(new String[0]), getSearchAnalyzer(searchQuery.isStem()));
      Query textQuery = parser.parse(searchQuery.getQuery());
      bqb.add(textQuery, BooleanClause.Occur.MUST);
      Query query = bqb.build();
      PriorityQueue<SearchHit<FluentBitEvent>> hits = searcher.search(query, new LogCollectorManager());
      log.info("Searched [{}] tweets for the query [{}]", hits.size(), query);
      List<SearchHit<FluentBitEvent>> results = new ArrayList<>();
      final int n = hits.size();
      searchQuery.setTopN(searchQuery.getTopN() == null || searchQuery.getTopN() <= 0 ? Integer.MAX_VALUE : searchQuery.getTopN());
      while (!hits.isEmpty() && (n - hits.size()) < searchQuery.getTopN()) {
        results.add(hits.poll());
      }
      return results;
    } catch (IOException | ParseException e) {
      log.error("Error Search for request{} : {}", searchQuery, e.getMessage());
      return List.of();
    }
  }

  @Override
  public IndexSearcher getIndexSearcher(String indexName) {
    return searchers.computeIfAbsent(indexName, index -> {
      try {
        Path indexPath = Paths.get(logitIndexDir + index);
        Directory directory = FSDirectory.open(indexPath);
        IndexReader indexReader = DirectoryReader.open(directory);
        return new IndexSearcher(indexReader);
      } catch (IOException e) {
        log.error("Error creating Index Searcher for index {} : {}", indexName, e.getMessage());
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
    searchers.forEach((index, searcher) -> {
      try {
        searcher.getIndexReader().close();
      } catch (IOException e) {
        log.error("Error closing IndexSearch for index {}: {}", index, e.getMessage());
      }
    });
    searchers.clear();
  }

  private final class LogCollectorManager implements CollectorManager<LogCollector, PriorityQueue<SearchHit<FluentBitEvent>>> {

    @Override
    public LogCollector newCollector() {
      return new LogCollector();
    }

    @Override
    public PriorityQueue<SearchHit<FluentBitEvent>> reduce(Collection<LogCollector> collectors) {
      final PriorityQueue<SearchHit<FluentBitEvent>> hits = new PriorityQueue<>(SearchHit::compareTo);
      for (LogCollector collector : collectors) {
        for (SearchHit<FluentBitEvent> hit : collector.getHits()) {
          hits.offer(hit);
        }
      }
      return hits;
    }
  }

  private final class LogCollector extends SimpleCollector {
    private LeafReaderContext context;
    private Scorable scorer;

    @Getter
    private final List<SearchHit<FluentBitEvent>> hits = new ArrayList<>();

    @Override
    public void doSetNextReader(LeafReaderContext context) {
      this.context = context;
    }

    @Override
    public void setScorer(Scorable scorer) {
      this.scorer = scorer;
    }

    @Override
    public void collect(int docId) throws IOException {
      //noinspection resource
      Document doc = context.reader().storedFields().document(docId); // this needs to be opened
      FluentBitEvent log = documentToLogEvent(doc);
      hits.add(new SearchHit<>(log, scorer.score(), context.docBase + docId));
    }

    @Override
    public ScoreMode scoreMode() {
      return ScoreMode.COMPLETE;
    }
  }

  private FluentBitEvent documentToLogEvent(Document document) {
    return FluentBitEvent.builder()
      .timestamp(dateUtil.convertToLocalDateTime(dateUtil.parseLong(document.get(IndexField.TIMESTAMP.name))))
      .clazz(document.get(IndexField.CLASS.name))
      .method(document.get(IndexField.METHOD.name))
      .level(document.get(IndexField.LEVEL.name))
      .message(document.get(IndexField.MESSAGE.name))
      .build();
  }

}
