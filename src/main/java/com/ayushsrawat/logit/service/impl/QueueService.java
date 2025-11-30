package com.ayushsrawat.logit.service.impl;

import com.ayushsrawat.logit.util.Constants;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.RollCycles;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Service
public class QueueService {

  private static final RollCycles CHRONICLE_ROLL_CYCLE = RollCycles.TWO_HOURLY;
  private static final DateTimeFormatter CHRONICLE_ROLL_CYCLE_FORMATTER = DateTimeFormatter.ofPattern(CHRONICLE_ROLL_CYCLE.format()).withZone(ZoneId.of("UTC"));

  private ChronicleQueue queue;
  private ThreadLocal<ExcerptAppender> threadLocalAppender; // cache excerptAppender per thread

  @Value("${logit.queue.dir}")
  private String queuePath;

  @PostConstruct
  public void init() {
    queue = SingleChronicleQueueBuilder.binary(queuePath) // wire as BINARY_LIGHT
      .timeoutMS(30_000L) // n * 3/2
      .rollCycle(CHRONICLE_ROLL_CYCLE).build();
    threadLocalAppender = ThreadLocal.withInitial(() -> queue.createAppender());
  }

  public ExcerptAppender getAppender() {
    return threadLocalAppender.get();
  }

  public ExcerptTailer getTailer(@NonNull String id) {
    return queue.createTailer(id);
  }

  @Scheduled(cron = "0 5 1/4 * * *")
  public void cleanupChronicleQueueDump() {
    log.info("Cleaning ChronicleQueue Log Dump at {}", LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER));
    Instant safeThreshold = Instant.now().minus(Duration.ofHours(4));
    log.info("Setting Safe Threshold at {}", safeThreshold.atZone(ZoneId.of("UTC")));
    try {
      Files.walkFileTree(Paths.get(queuePath), new SimpleFileVisitor<>() {
        @Override
        public @NonNull FileVisitResult visitFile(@NonNull Path path, @NonNull BasicFileAttributes attrs) {
          String fileName = path.getFileName().toString();
          if (fileName.endsWith(SingleChronicleQueue.SUFFIX)) {
            log.info("Visiting: {}", fileName);
            try {
              // "20251130-14II.cq4" -> "20251130-14II"
              String datePart = fileName.substring(0, fileName.lastIndexOf("."));
              Instant fileTime = CHRONICLE_ROLL_CYCLE_FORMATTER.parse(datePart, Instant::from);
              if (fileTime.isBefore(safeThreshold)) {
                log.info("Deleting old file : {}", fileName);
                Files.delete(path);
              }
            } catch (DateTimeParseException dpe) {
              log.error("Skipping unknown file format: {}", fileName, dpe);
            } catch (IOException e) {
              log.error("Could not delete: {}", fileName, e);
            }
          }
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      log.error("Error while cleanupChronicleQueueDump scheduler", e);
    }
  }

  @PreDestroy
  public void close() {
    if (queue != null && !queue.isClosed()) {
      queue.close();
    }
  }

}
