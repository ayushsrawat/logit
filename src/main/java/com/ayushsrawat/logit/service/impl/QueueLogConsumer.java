package com.ayushsrawat.logit.service.impl;

import com.ayushsrawat.logit.payload.request.FluentBitEvent;
import com.ayushsrawat.logit.service.IndexingService;
import com.ayushsrawat.logit.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.threads.Pauser;
import net.openhft.chronicle.wire.DocumentContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueLogConsumer implements CommandLineRunner {

  private final QueueService queueService;
  private final IndexingService<FluentBitEvent> fbIndexingService;
  private final ObjectMapper objectMapper;

  private final Pauser pauser = Pauser.balanced();
  private volatile boolean running = true;

  @Override
  public void run(String... args) {
    ExcerptTailer tailer = queueService.getTailer("logit-indexer-consumer");
    log.info("Starting Chronicle Queue Consumer... Tailing from {}", tailer.queue().fileAbsolutePath());
    Thread thread = new Thread(() -> {
      while (running) {
        try (DocumentContext context = tailer.readingDocument()) {
          if (context.isPresent()) {
            String payload = Objects.requireNonNull(context.wire()).read(Constants.CHRONICLE_QUEUE_KEY).text();
            processPayload(payload);
            pauser.reset();
          } else {
            pauser.pause();
          }
        }
      }
    });
    thread.setName("LogConsumer");
    thread.start();
  }

  private void processPayload(String jsonPayload) {
    try {
      JsonNode payload = objectMapper.readTree(jsonPayload);
      List<FluentBitEvent> logs = fbIndexingService.parseEvents(payload);
      log.info("Parsed {} logs", logs.size());
      int indexed = fbIndexingService.indexLogEvents(logs);
      log.info("Indexed {} events", indexed);
    } catch (JsonProcessingException e) {
      log.error("Failed to Parse/Index payload", e);
    }
  }

  @PreDestroy
  public void close() {
    this.running = false;
  }

}
