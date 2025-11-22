package com.ayushsrawat.logit.service.impl;

import com.ayushsrawat.logit.payload.request.FluentBitEvent;
import com.ayushsrawat.logit.service.IndexingService;
import com.ayushsrawat.logit.tools.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class FluentBitIndexingService implements IndexingService<FluentBitEvent> {

  private static final Logger log = LoggerFactory.getLogger(FluentBitIndexingService.class);

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
      try {
        var time = LocalDateTime.parse(timestamp, Constants.DATE_TIME_FORMATTER);
        event.setTimestamp(time);
      } catch (DateTimeParseException pe) {
        log.error("Unable to parse timestamp {}", timestamp);
        return null;
      }
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
    return event;
  }

  @Override
  public Integer indexLogs(List<FluentBitEvent> events) {
    log.info("Indexing {} fluent bit log events", events.size());
    return events.size();
  }

}
