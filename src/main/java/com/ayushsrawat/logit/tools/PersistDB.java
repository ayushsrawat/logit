package com.ayushsrawat.logit.tools;

import com.fasterxml.jackson.databind.JsonNode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class PersistDB {

  // requires postgresql database driver in classpath
  private void saveFluentEventToDB(JsonNode payload) {
    String url = "jdbc:postgresql://localhost:5432/logit";
    String user = "postgres";
    String password = "password";
    try (Connection connection = DriverManager.getConnection(url, user, password)) {
      for (JsonNode node : payload) {
        String timestamp = "", clazz = "", method = "", level = "", log = "";
        if (node.has("timestamp")) {
          timestamp = node.get("timestamp").asText();
        }
        if (node.has("class")) {
          clazz = node.get("class").asText();
        }
        if (node.has("method")) {
          method = node.get("method").asText();
        }
        if (node.has("level")) {
          level = node.get("level").asText();
        }
        if (node.has("msg")) {
          log = node.get("msg").asText();
        }
        String sql = "insert into log (timestamp, clazz, method, level, log, created_at) values (?,?,?,?,?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, timestamp);
        statement.setString(2, clazz);
        statement.setString(3, method);
        statement.setString(4, level);
        statement.setString(5, log);
        statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
        statement.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
