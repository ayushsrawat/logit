package com.ayushsrawat.logit.controller;

import com.ayushsrawat.logit.payload.request.FluentBitEvent;
import com.ayushsrawat.logit.payload.response.IndexCounterDTO;
import com.ayushsrawat.logit.service.IndexingService;
import com.ayushsrawat.logit.service.impl.QueueService;
import com.ayushsrawat.logit.util.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.wire.DocumentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("api/index")
@RequiredArgsConstructor
public class IndexController {

  private static final Logger log = LoggerFactory.getLogger(IndexController.class);

  private final QueueService queueService;
  private final IndexingService<FluentBitEvent> fbIndexingService;

  @PostMapping("/fluent")
  public ResponseEntity<Void> indexFluentBitLogs(@RequestBody JsonNode payload) {
    log.info("Received {} logs", payload.size());
    ExcerptAppender appender = queueService.getAppender();
    try (DocumentContext context = appender.writingDocument()) {
      Objects.requireNonNull(context.wire()).write(Constants.CHRONICLE_QUEUE_KEY).text(payload.toString());
    }
    return ResponseEntity.ok().build();
  }

  @GetMapping("/count")
  public ResponseEntity<IndexCounterDTO> countIndexDocs(@RequestParam(value = "i") String index) {
    Integer count = fbIndexingService.docsCount(index);
    return ResponseEntity.ok(IndexCounterDTO.builder().index(index).count(count).build());
  }

}
