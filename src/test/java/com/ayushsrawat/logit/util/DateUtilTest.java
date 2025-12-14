package com.ayushsrawat.logit.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtilTest {

  private static final Logger log = LoggerFactory.getLogger(DateUtilTest.class);
  private static final Pattern TIME_AGO_PATTERN = Pattern.compile("(?<time>[\\d]+)[\\s]*(?<unit>[\\w]*)[\\s]*ago");

  @Test
  public void testTimeAgoPattern() {
    log.info("Testing Time Ago Pattern");
    String times = "13 minutes ago\n" +
        "2 seconds ago\n" +
        "4 hrs ago\n" +
        "12 days ago\n" +
        "1 day ago\n" +
        "1day ago";
    Matcher matcher = TIME_AGO_PATTERN.matcher(times);
    while (matcher.find()) {
      log.info("Time: {}", matcher.group("time"));
      log.info("Unit: {}", matcher.group("unit"));
    }
  }

}
