package com.ayushsrawat.logit.controller;

import com.ayushsrawat.logit.payload.request.FluentBitEvent;
import com.ayushsrawat.logit.service.IndexingService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/index")
@RequiredArgsConstructor
public class IndexController {

  private static final Logger log = LoggerFactory.getLogger(IndexController.class);

  private final IndexingService<FluentBitEvent> fluentBitService;

  @PostMapping("/fluent")
  public ResponseEntity<Void> fluentBit(@RequestBody JsonNode payload) {
    log.info("Received {} logs", payload.size());
    List<FluentBitEvent> events = fluentBitService.parseEvents(payload);
    log.info("Parsed {} logs", events.size());
    int indexed = fluentBitService.indexLogs(events);
    log.info("Index {} events", indexed);
    return ResponseEntity.ok().build();
  }

}
