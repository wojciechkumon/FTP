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
 * FTP APPE command
 * @author Wojciech Kumoń
 *
 */
public class AppeCommand implements Command {
  private AppeCommand() {}

  /**
   * Appe command factory method
   * @param status client status
   * @param command full command
   * @param dataSocket data socket
   * @param db database
   * @return new instance of appe command
   */
  public static AppeCommand newInstance(ClientStatus status, String command, DataSocket dataSocket,
      Database db) {
    if (status.getLoginStatus() == LoginStatus.NOT_LOGGED_IN) {
      return new AppeCommand();
    }
    if (dataSocket.isDataConnectionSet()) {
      return new AppeCommandLoggedIn(status, command, dataSocket, db);
    }
    return new AppeCommandLoggedInNoSocket();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.BAD_SEQUENCE_503;
  }


  private static class AppeCommandLoggedIn extends AppeCommand {
    private ClientStatus status;
    private String fileName;
    private DataSocket dataSocket;
    private Database db;

    private AppeCommandLoggedIn(ClientStatus status, String command, DataSocket dataSocket,
        Database db) {
      this.status = status;
      Matcher matcher = CommandPatterns.APPE_PATTERN.matcher(command);
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
      boolean fileExists = fileExists(fileFullPath, relativePath);
      if (fileExists && Files.isDirectory(fileFullPath)) {
        return String.format(FtpResponses.FILE_IS_A_DIRECTORY_550, fileName);
      }
      if (fileExists && !correctPermissions(relativePath, fileExists)) {
        return FtpResponses.PERMISSION_DENIED_550;
      }
      if (dataSocket.isActiveModeSet()) {
        try {
          dataSocket.connectActive();
        } catch (IOException e) {
          return FtpResponses.CANT_OPEN_DATA_CONNECTION_425;
        }
      }
      SaveFileTask task = new SaveFileTask(status, dataSocket, db, fileFullPath);
      if (fileExists) {
        task.setAppendMode(true);
      }
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

    private boolean correctPermissions(Path relativePath, boolean fileExists) {
      if (fileExists) {
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

  private static class AppeCommandLoggedInNoSocket extends AppeCommand {
    private AppeCommandLoggedInNoSocket() {}

    @Override
    public String execute() {
      return FtpResponses.USE_PORT_OR_PASV_425;
    }
  }

}
