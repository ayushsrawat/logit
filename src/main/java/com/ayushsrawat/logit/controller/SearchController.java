package com.ayushsrawat.logit.controller;

import com.ayushsrawat.logit.lucene.SearchHit;
import com.ayushsrawat.logit.payload.request.FluentBitEvent;
import com.ayushsrawat.logit.payload.request.SearchRequest;
import com.ayushsrawat.logit.payload.response.IndexCountDTO;
import com.ayushsrawat.logit.service.SearchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/search")
@RequiredArgsConstructor
public class SearchController {

  private final SearchingService<SearchHit<FluentBitEvent>> fbSearchingService;

  @PostMapping("/fluent")
  public ResponseEntity<List<SearchHit<FluentBitEvent>>> searchFluentBitLogs(@RequestBody SearchRequest searchRequest) {
    var hits = fbSearchingService.search(searchRequest);
    return ResponseEntity.ok().body(hits);
  }

  @GetMapping("/stats/ic")
  public ResponseEntity<List<IndexCountDTO>> indexCountStats() {
    var stats = fbSearchingService.indexCountStats();
    return ResponseEntity.ok().body(stats);
  }

}
