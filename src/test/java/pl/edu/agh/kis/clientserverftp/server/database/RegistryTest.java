package pl.edu.agh.kis.clientserverftp.server.database;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

public class RegistryTest {

  @Test
  public void testRegistry() throws SQLException {
    String name = "test";
    String dbUrl = "jdbc:h2:~/" + name + ";MV_STORE=FALSE;MVCC=FALSE";
    String dbUsername = "root";
    String dbPassword = "pass";
    
    Registry.setConnectionData(dbUrl, dbUsername, dbPassword);
    Connection connection = Registry.createConnection();
    
    assertTrue(connection.getCatalog().equalsIgnoreCase(name));
    cleanUp(connection);
  }

  private void cleanUp(Connection connection) throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      stmt.executeUpdate("DROP ALL OBJECTS DELETE FILES");
    }
    connection.close();
  }

}
