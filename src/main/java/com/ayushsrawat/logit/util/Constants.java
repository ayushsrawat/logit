package com.ayushsrawat.logit.util;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public interface Constants {

  DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm:ss a", Locale.ENGLISH);

}
