package com.ayushsrawat.logit.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

  private ChronicleQueue queue;
  private ThreadLocal<ExcerptAppender> threadLocalAppender; // cache excerptAppender per thread

  @Value("${logit.queue.dir}")
  private String queuePath;

  @PostConstruct
  public void init() {
    queue = SingleChronicleQueueBuilder.binary(queuePath).timeoutMS(30_000L).build();
    threadLocalAppender = ThreadLocal.withInitial(() -> queue.createAppender());
  }

  public ExcerptAppender getAppender() {
    return threadLocalAppender.get();
  }

  public ExcerptTailer getTailer() {
    return queue.createTailer();
  }

  @PreDestroy
  public void close() {
    if (queue != null && !queue.isClosed()) {
      queue.close();
    }
  }

}
