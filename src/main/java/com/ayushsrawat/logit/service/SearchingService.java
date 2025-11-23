package com.ayushsrawat.logit.service;

import com.ayushsrawat.logit.lucene.SearchHit;
import com.ayushsrawat.logit.payload.request.SearchRequest;

import java.util.List;

public interface SearchingService <T extends SearchHit<?>> {

  List<T> search(SearchRequest searchRequest);

}
