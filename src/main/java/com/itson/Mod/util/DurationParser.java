package com.itson.Mod.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DurationParser {

  private static final Pattern SEGMENT = Pattern.compile("(\\d+)([smhdw])");
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  private DurationParser() {
  }

  public static Duration parse(String input) {
    String text = input.toLowerCase(Locale.ROOT);
    Matcher matcher = SEGMENT.matcher(text);
    Duration duration = Duration.ZERO;
    int matched = 0;
    int end = 0;
    while (matcher.find()) {
      if (matcher.start() != end) {
        return null;
      }
      long amount = Long.parseLong(matcher.group(1));
      duration = duration.plus(switch (matcher.group(2)) {
        case "s" ->
          Duration.ofSeconds(amount);
        case "m" ->
          Duration.ofMinutes(amount);
        case "h" ->
          Duration.ofHours(amount);
        case "d" ->
          Duration.ofDays(amount);
        case "w" ->
          Duration.ofDays(amount * 7L);
        default ->
          throw new IllegalStateException("unreachable");
      });
      matched++;
      end = matcher.end();
    }
    if (matched == 0 || end != text.length()) {
      return null;
    }
    return duration;
  }

  public static String formatExpiry(Duration duration, String permanentText) {
    if (duration == null) {
      return permanentText;
    }
    return DATE_FORMAT.format(LocalDateTime.now().plus(duration));
  }

  public static String toCompact(Duration duration) {
    long days = duration.toDays();
    long hours = duration.toHoursPart();
    long minutes = duration.toMinutesPart();
    long seconds = duration.toSecondsPart();
    StringBuilder builder = new StringBuilder();
    if (days > 0L) {
      builder.append(days).append('d');
    }
    if (hours > 0L) {
      builder.append(hours).append('h');
    }
    if (minutes > 0L) {
      builder.append(minutes).append('m');
    }
    if (seconds > 0L) {
      builder.append(seconds).append('s');
    }
    return builder.isEmpty() ? "0s" : builder.toString();
  }
}
