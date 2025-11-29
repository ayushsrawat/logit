package com.ayushsrawat.logit.benchmark;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CrashTest {

  private static final String URL = "http://localhost:3338/api/index/fluent";
  private static final int THREADS = 300;
  private static final int REQUESTS_PER_THREAD = 100;

  static void main() {
    ExecutorService executor = Executors.newFixedThreadPool(THREADS);
    HttpClient client = HttpClient.newHttpClient();

    String payload = generateHeavyPayload(1000); // 1000 log lines per request

    System.out.println("Starting Attack with " + THREADS + " threads...");

    for (int i = 0; i < THREADS; i++) {
      executor.submit(() -> {
        for (int j = 0; j < REQUESTS_PER_THREAD; j++) {
          try {
            HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create(URL))
              .header("Content-Type", "application/json")
              .POST(HttpRequest.BodyPublishers.ofString(payload))
              .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status: " + response.statusCode());
          } catch (Exception e) {
            System.err.println("Request Failed: " + e.getMessage());
          }
        }
      });
    }

    executor.shutdown();
    try {
      executor.awaitTermination(10, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static String generateHeavyPayload(int lines) {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 0; i < lines; i++) {
      sb.append(String.format("""
                {
                    "timestamp": "Nov 25, 2025 08:58:21 AM",
                    "service_name": "crash-service",
                    "class": "com.example.CrashTest",
                    "method": "attack",
                    "level": "ERROR",
                    "msg": "This is a very long log message to fill up memory and take time to index. We want to simulate a heavy load on the system. %d"
                }
            """, i));
      if (i < lines - 1) sb.append(",");
    }
    sb.append("]");
    return sb.toString();
  }
}
