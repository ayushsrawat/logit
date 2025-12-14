package com.ayushsrawat.logit.util;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public interface Constants {

  DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm:ss a", Locale.ENGLISH);
  DateTimeFormatter ISO_T_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
  DateTimeFormatter ISO_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS", Locale.ENGLISH);
  DateTimeFormatter SIMPLE_ISO_8601_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

  String CHRONICLE_QUEUE_KEY = "payload";

}
