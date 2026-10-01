package com.renanloureiroo.hexagonal.testsupport.database;

import java.util.List;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.jdbc.core.simple.JdbcClient;

@TestComponent
public class DatabaseCleaner {

  private static final String TABLES_QUERY =
      """
      select table_name
        from information_schema.tables
       where table_schema = current_schema()
         and table_type = 'BASE TABLE'
         and table_name <> 'flyway_schema_history'
      """;

  private final JdbcClient jdbc;
  private List<String> tables;

  public DatabaseCleaner(JdbcClient jdbc) {
    this.jdbc = jdbc;
  }

  public void clean() {
    if (tables == null) {
      tables = jdbc.sql(TABLES_QUERY).query(String.class).list();
    }
    if (!tables.isEmpty()) {
      jdbc.sql("truncate table " + String.join(", ", tables) + " restart identity cascade")
          .update();
    }
  }
}
