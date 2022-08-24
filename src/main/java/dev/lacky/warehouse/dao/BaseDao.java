package dev.lacky.warehouse.dao;

import dev.lacky.warehouse.connection.ConnectionBuilder;
import java.sql.Connection;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseDao {

  protected static final Logger logger = LoggerFactory.getLogger(ProductDao.class);
  private ConnectionBuilder connectionBuilder;

  protected Connection getConnection() throws SQLException {
    return connectionBuilder.getConnection();
  }

  public void setConnectionBuilder(ConnectionBuilder connectionBuilder) {
    this.connectionBuilder = connectionBuilder;
  }
}
