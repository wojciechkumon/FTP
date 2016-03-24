package pl.edu.agh.kis.clientserverftp.server.database;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import pl.edu.agh.kis.clientserverftp.server.ServerProperties;

/**
 * Controlls connections with database.
 * @author Wojciech Kumoń
 */
public class Registry {
  private static String dbUrl;
  private static String dbUsername;
  private static String dbPassword;

  static {
    setDefaultConnectionData();
  }

  /**
   * Returns new connections with database. Uses url, username and password which are
   * set at present.
   * @return created connection with database
   */
  public static Connection createConnection() {
    try {
      return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sets default database connection data.
   */
  public static void setDefaultConnectionData() {
    Path path = Paths.get(ServerProperties.getDatabaseDirectoryPath()).toAbsolutePath().normalize();
    dbUrl = "jdbc:h2:file:" + path + "/ftp_db;MV_STORE=FALSE;MVCC=FALSE";
    dbUsername = "root";
    dbPassword = "pass";
  }

  /**
   * Allows setting connection data manually.
   * @param dbUrl database url
   * @param username database username
   * @param password database password
   */
  public static void setConnectionData(String dbUrl, String username, String password) {
    Registry.dbUrl = dbUrl;
    dbUsername = username;
    dbPassword = password;
  }

}
