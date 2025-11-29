package com.ayushsrawat.logit.controller;

import com.ayushsrawat.logit.payload.request.FluentBitEvent;
import com.ayushsrawat.logit.payload.response.IndexCounterDTO;
import com.ayushsrawat.logit.service.IndexingService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/index")
@RequiredArgsConstructor
public class IndexController {

  private static final Logger log = LoggerFactory.getLogger(IndexController.class);

  private final IndexingService<FluentBitEvent> fbIndexingService;

  @PostMapping("/fluent")
  public ResponseEntity<Void> indexFluentBitLogs(@RequestBody JsonNode payload) {
    log.info("Received {} logs", payload.size());
    List<FluentBitEvent> events = fbIndexingService.parseEvents(payload);
    log.info("Parsed {} logs", events.size());
    int indexed = fbIndexingService.indexLogEvents(events);
    log.info("Indexed {} events", indexed);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/count")
  public ResponseEntity<IndexCounterDTO> countIndexDocs(@RequestParam(value = "i") String index) {
    Integer count = fbIndexingService.docsCount(index);
    return ResponseEntity.ok(IndexCounterDTO.builder().index(index).count(count).build());
  }

}
