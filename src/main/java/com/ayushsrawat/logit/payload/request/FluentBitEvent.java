package com.ayushsrawat.logit.payload.request;

import com.ayushsrawat.logit.payload.event.LogEvent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Getter
@Setter
public class FluentBitEvent implements LogEvent {

  private LocalDateTime timestamp;
  private String serviceName;
  private String clazz;
  private String method;
  private String message;
  private String level;

  @Override
  public @NonNull String toString() {
    return "FluentBitEvent{" +
      "timestamp=" + timestamp +
      ", serviceName='" + serviceName + '\'' +
      ", clazz='" + clazz + '\'' +
      ", method='" + method + '\'' +
      ", level='" + level + '\'' +
      ", message='" + message + '\'' +
      '}';
  }

}
