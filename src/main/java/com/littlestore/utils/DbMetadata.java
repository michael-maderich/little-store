package com.littlestore.utils;

import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.*;

@Component
public class DbMetadata {

  private final DataSource ds;

  public DbMetadata(DataSource ds) {
    this.ds = ds;
  }

  public int getColumnSize(String tableName, String columnName, int fallback) {
    try (Connection conn = ds.getConnection()) {
      DatabaseMetaData md = conn.getMetaData();
      try (ResultSet rs = md.getColumns(conn.getCatalog(), null, tableName, columnName)) {
        if (rs.next()) {
          return rs.getInt("COLUMN_SIZE");
        }
      }
    } catch (SQLException e) {
      // log error
    }
    return fallback;
  }
}
