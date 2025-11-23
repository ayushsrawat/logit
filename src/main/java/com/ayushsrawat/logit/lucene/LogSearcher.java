package com.ayushsrawat.logit.lucene;

import com.ayushsrawat.logit.payload.request.SearchRequest;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.IndexSearcher;

import java.util.List;

public interface LogSearcher<T extends SearchHit<?>> {

  List<T> search(SearchRequest request);

  /// INTERNAL

  IndexSearcher getIndexSearcher(String index);

  Analyzer getSearchAnalyzer(boolean stem);

}
