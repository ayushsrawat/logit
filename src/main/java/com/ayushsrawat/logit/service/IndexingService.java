package com.ayushsrawat.logit.service;

import com.ayushsrawat.logit.payload.event.LogEvent;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public interface IndexingService<T extends LogEvent> {

  T parseEvent(JsonNode node);

  Integer indexLogs(List<T> events);

  default List<T> parseEvents(JsonNode nodes) {
    List<T> events = new ArrayList<>();
    nodes.forEach(n -> {
      T event = parseEvent(n);
      if (event != null) {
        events.add(event);
      }
    });
    return events;
  }

}
