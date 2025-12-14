package com.ayushsrawat.logit.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DateUtil {

  private static final Pattern TIME_AGO_PATTERN = Pattern.compile("(?<time>[\\d]+)[\\s]*(?<unit>[\\w]*)[\\s]*ago");
  private static final List<DateTimeFormatter> FORMATTERS = List.of(
      Constants.ISO_T_FORMATTER,
      Constants.DATE_TIME_FORMATTER,
      Constants.ISO_DATE_TIME_FORMATTER,
      DateTimeFormatter.ISO_DATE
  );

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
    for (DateTimeFormatter formatter : FORMATTERS) {
      try {
        return LocalDateTime.parse(timestamp, formatter);
      } catch (DateTimeParseException _) {}
    }
    try {
      return LocalDate.parse(timestamp, Constants.SIMPLE_ISO_8601_DATE_FORMATTER).atStartOfDay();
    } catch (DateTimeParseException _) {}
    return null;
  }

  /**
   * Resolves the "12 minutes ago" search times to unix epoch
   * @return unix epoch long time otherwise 0L
   */
  public long resolveTime(String searchTime) {
    if (searchTime == null) return 0L;
    if ("now".equalsIgnoreCase(searchTime)) {
      return System.currentTimeMillis();
    }
    Matcher matcher = TIME_AGO_PATTERN.matcher(searchTime);
    if (matcher.find()) {
      int timeAgo = Integer.parseInt(matcher.group("time"));
      String unit = matcher.group("unit");
      ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(ZoneId.systemDefault());
      return switch (unit.toLowerCase()) {
        case "secs", "seconds", "sec" -> zonedDateTime.minusSeconds(timeAgo).toInstant().toEpochMilli();
        case "mins", "min", "minutes" -> zonedDateTime.minusMinutes(timeAgo).toInstant().toEpochMilli();
        case "hrs", "hours", "hs" -> zonedDateTime.minusHours(timeAgo).toInstant().toEpochMilli();
        case "days", "day" -> zonedDateTime.minusDays(timeAgo).toInstant().toEpochMilli();
        case "weeks", "week" -> zonedDateTime.minusWeeks(timeAgo).toInstant().toEpochMilli();
        case "months", "month" -> zonedDateTime.minusMonths(timeAgo).toInstant().toEpochMilli();
        case "years", "yrs", "year" -> zonedDateTime.minusYears(timeAgo).toInstant().toEpochMilli();
        default -> throw new IllegalArgumentException("Unknown time unit: " + unit);
      };
    }
    LocalDateTime parsedDate = parseDate(searchTime);
    if (parsedDate == null) {
      throw new IllegalArgumentException("Invalid date format: " + searchTime);
    }
    return parsedDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }

}
