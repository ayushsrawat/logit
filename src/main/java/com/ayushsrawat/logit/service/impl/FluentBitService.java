package com.ayushsrawat.logit.service.impl;

import com.ayushsrawat.logit.lucene.LogIndexer;
import com.ayushsrawat.logit.lucene.LogSearcher;
import com.ayushsrawat.logit.lucene.SearchHit;
import com.ayushsrawat.logit.payload.request.FluentBitEvent;
import com.ayushsrawat.logit.payload.request.SearchRequest;
import com.ayushsrawat.logit.payload.response.IndexCountDTO;
import com.ayushsrawat.logit.service.IndexingService;
import com.ayushsrawat.logit.service.SearchingService;
import com.ayushsrawat.logit.util.DateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FluentBitService implements IndexingService<FluentBitEvent>, SearchingService<SearchHit<FluentBitEvent>> {

  private static final Logger log = LoggerFactory.getLogger(FluentBitService.class);

  private final LogIndexer<FluentBitEvent> logIndexer;
  private final LogSearcher<SearchHit<FluentBitEvent>> logSearcher;
  private final DateUtil dateUtil;

  @Value("${logit.index.dir}")
  private String logitIndexDir;

  private enum Fields {
    TIMESTAMP("timestamp"),
    SERVICE("service_name"),
    CLAZZ("class"),
    METHOD("method"),
    LEVEL("level"),
    MESSAGE("msg");
    private final String name;

    Fields(String name) {
      this.name = name;
    }
  }

  @Override
  public FluentBitEvent parseEvent(JsonNode node) {
    FluentBitEvent event = new FluentBitEvent();
    if (node.has(Fields.TIMESTAMP.name)) {
      String timestamp = node.get(Fields.TIMESTAMP.name).asText();
      var t = dateUtil.parseDate(timestamp);
      if (t == null) {
        log.error("Unable to parse timestamp {}", timestamp);
        return null;
      }
      event.setTimestamp(t);
    }
    if (node.has(Fields.SERVICE.name)) {
      event.setServiceName(node.get(Fields.SERVICE.name).asText());
    }
    if (node.has(Fields.CLAZZ.name)) {
      event.setClazz(node.get(Fields.CLAZZ.name).asText());
    }
    if (node.has(Fields.METHOD.name)) {
      event.setMethod(node.get(Fields.METHOD.name).asText());
    }
    if (node.has(Fields.LEVEL.name)) {
      event.setLevel(node.get(Fields.LEVEL.name).asText());
    }
    if (node.has(Fields.MESSAGE.name)) {
      event.setMessage(node.get(Fields.MESSAGE.name).asText());
    }
    if (event.isIncomplete()) {
      log.warn("Incomplete FluentBit Event in service: {}", node.get(Fields.SERVICE.name));
      return null;
    }
    return event;
  }

  @Override
  public Integer indexLogEvents(List<FluentBitEvent> events) {
    Map<String, List<FluentBitEvent>> eventsByIndex = events.stream().collect(Collectors.groupingBy(FluentBitEvent::getServiceName));
    int indexed  = 0;
    for (Map.Entry<String, List<FluentBitEvent>> entry : eventsByIndex.entrySet()) {
      indexed += logIndexer.index(entry.getKey(), entry.getValue());
    }
    return indexed;
  }

  @Override
  public Integer docsCount(String indexName) {
    return logIndexer.docsCount(indexName);
  }

  ///
  /// SEARCHING
  ///

  @Override
  public List<SearchHit<FluentBitEvent>> search(SearchRequest searchRequest) {
    List<SearchHit<FluentBitEvent>> hits = logSearcher.search(searchRequest);
    log.info("Searched {} for request: {}", hits.size(), searchRequest);
    return hits;
  }

  @Override
  public List<IndexCountDTO> indexCountStats() {
    List<IndexCountDTO> indexCounts = new ArrayList<>();
    try {
      Files.walkFileTree(Paths.get(logitIndexDir), EnumSet.noneOf(FileVisitOption.class), 1, new SimpleFileVisitor<>() {
        @Override
        public @NonNull FileVisitResult visitFile(@NonNull Path path, @NonNull BasicFileAttributes attrs) {
          if (Files.isDirectory(path)) {
            String index = path.getFileName().toString();
            int docsCount = docsCount(index);
            indexCounts.add(IndexCountDTO.builder().index(index).count(docsCount).build());
          }
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      log.error("Error while counting log indexes", e);
    }
    return indexCounts;
  }

}
