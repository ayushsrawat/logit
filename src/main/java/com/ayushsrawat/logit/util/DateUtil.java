package com.ayushsrawat.logit.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.TimeZone;

@Component
public class DateUtil {

  public long convertToLong(LocalDateTime localDateTime) {
    if (localDateTime == null) return 0L;
    return localDateTime.atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli();
  }

  public LocalDateTime convertToLocalDateTime(long time) {
    if (time == 0L) return null;
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), TimeZone.getDefault().toZoneId());
  }

  @SuppressWarnings("unused")
  public Date convertToDate(LocalDateTime localDateTime) {
    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
  }

  public Long parseLong(String value) {
    try {
      return value == null || value.isBlank() ? 0L : Long.parseLong(value.trim());
    } catch (NumberFormatException e) {
      return 0L;
    }
  }

  public LocalDateTime parseDate(String timestamp) {
    if (timestamp == null) return null;
    try {
      return LocalDateTime.parse(timestamp, Constants.DATE_TIME_FORMATTER);
    } catch (DateTimeParseException e) {
      try {
        return LocalDateTime.parse(timestamp, Constants.ISO_DATE_TIME_FORMATTER);
      } catch (DateTimeParseException e2) {
        return null;
      }
    }
  }

}
