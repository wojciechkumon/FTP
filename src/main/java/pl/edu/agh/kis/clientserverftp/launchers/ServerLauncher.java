package pl.edu.agh.kis.clientserverftp.launchers;

import java.sql.Connection;
import java.sql.SQLException;

import pl.edu.agh.kis.clientserverftp.server.Server;
import pl.edu.agh.kis.clientserverftp.server.database.Database;
import pl.edu.agh.kis.clientserverftp.server.database.Registry;

/**
 * Server launcher class.
 * 
 * @author Wojciech Kumoń
 */
public class ServerLauncher {

  public static void main(String[] args) {
    initDataBaseIfNotExist();
    startServer();
  }

  private static void initDataBaseIfNotExist() {
    Database db = Database.getInstance();
    try (Connection connection = Registry.createConnection()) {
      db.createTables(connection);
      db.createAdminGroup(connection);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static void startServer() {
    new Thread(new Server()).start();
  }

}
