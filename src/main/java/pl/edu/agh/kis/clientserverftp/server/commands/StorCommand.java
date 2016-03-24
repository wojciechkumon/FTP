package pl.edu.agh.kis.clientserverftp.server.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;

import pl.edu.agh.kis.clientserverftp.server.ClientStatus;
import pl.edu.agh.kis.clientserverftp.server.DataSocket;
import pl.edu.agh.kis.clientserverftp.server.FtpResponses;
import pl.edu.agh.kis.clientserverftp.server.LoginStatus;
import pl.edu.agh.kis.clientserverftp.server.database.Database;
import pl.edu.agh.kis.clientserverftp.server.database.Registry;

/**
 * FTP STOR command
 * @author Wojciech Kumoń
 *
 */
public class StorCommand implements Command {
  private StorCommand() {}

  /**
   * STOR command factory method
   * @param status client status
   * @param command full command
   * @param dataSocket data socket
   * @param db database
   * @return new instance of STOR command
   */
  public static StorCommand newInstance(ClientStatus status, String command, DataSocket dataSocket,
      Database db) {
    if (status.getLoginStatus() == LoginStatus.NOT_LOGGED_IN) {
      return new StorCommand();
    }
    if (dataSocket.isDataConnectionSet()) {
      return new StorCommandLoggedIn(status, command, dataSocket, db);
    }
    return new StorCommandLoggedInNoSocket();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.BAD_SEQUENCE_503;
  }


  private static class StorCommandLoggedIn extends StorCommand {
    private ClientStatus status;
    private String fileName;
    private DataSocket dataSocket;
    private Database db;

    private StorCommandLoggedIn(ClientStatus status, String command, DataSocket dataSocket,
        Database db) {
      this.status = status;
      Matcher matcher = CommandPatterns.STOR_PATTERN.matcher(command);
      if (matcher.matches()) {
        fileName = matcher.group(1);
      }
      this.dataSocket = dataSocket;
      this.db = db;
    }

    @Override
    public String execute() {
      Path fileFullPath = status.getAbsoluteResolvedPath(Paths.get(fileName));
      Path relativePath = status.getFtpRelativePath(fileFullPath);
      boolean exists = fileExists(fileFullPath, relativePath);
      if (exists && Files.isDirectory(fileFullPath)) {
        return String.format(FtpResponses.FILE_IS_A_DIRECTORY_550, fileName);
      }
      if (exists && !correctPermissions(fileFullPath, relativePath)) {
        return FtpResponses.PERMISSION_DENIED_550;
      }
      if (dataSocket.isActiveModeSet()) {
        try {
          dataSocket.connectActive();
        } catch (IOException e1) {
          return FtpResponses.CANT_OPEN_DATA_CONNECTION_425;
        }
      }
      
      DeleCommand.newInstance(status, "DELE " + relativePath.toString(), db).execute();
      StoppableTask task = new SaveFileTask(status, dataSocket, db, fileFullPath);
      dataSocket.startThread(task);
      return String.format(FtpResponses.OPENING_BINARY_CONNECTION_150, fileName);
    }

    private boolean fileExists(Path fileFullPath, Path relativePath) {
      try (Connection connection = Registry.createConnection()) {
        return Files.exists(fileFullPath)
            && Database.getInstance().fileExists(connection, relativePath.toString());
      } catch (SQLException e) {
        e.printStackTrace();
        return false;
      }
    }

    private boolean correctPermissions(Path fileFullPath, Path relativePath) {
      if (Files.exists(fileFullPath)) {
        Database db = Database.getInstance();
        try (Connection connection = Registry.createConnection()) {
          int userId = db.getUserId(connection, status.getUsername());
          return db.canWrite(connection, userId, relativePath.toString());
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      return true;
    }

  }

  private static class StorCommandLoggedInNoSocket extends StorCommand {
    private StorCommandLoggedInNoSocket() {}

    @Override
    public String execute() {
      return FtpResponses.USE_PORT_OR_PASV_425;
    }
  }

}
