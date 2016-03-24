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
import pl.edu.agh.kis.clientserverftp.server.database.FileInfo;
import pl.edu.agh.kis.clientserverftp.server.database.Registry;

/**
 * FTP RETR command
 * @author Wojciech Kumoń
 *
 */
public class RetrCommand implements Command {
  private RetrCommand() {}

  /**
   * RETR command factory method
   * @param status client status
   * @param command full command
   * @param dataSocket data socket
   * @param db database
   * @return new instance of RETR command
   */
  public static RetrCommand newInstance(ClientStatus status, String command, DataSocket dataSocket,
      Database db) {
    if (status.getLoginStatus() == LoginStatus.NOT_LOGGED_IN) {
      return new RetrCommand();
    }
    if (dataSocket.isDataConnectionSet()) {
      return new RetrCommandLoggedIn(status, command, dataSocket, db);
    }
    return new RetrCommandLoggedInNoSocket();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.BAD_SEQUENCE_503;
  }

  private static class RetrCommandLoggedIn extends RetrCommand {
    private ClientStatus status;
    private String fileName;
    private DataSocket dataSocket;
    private Database db;

    private RetrCommandLoggedIn(ClientStatus status, String command, DataSocket dataSocket,
        Database db) {
      this.status = status;
      Matcher matcher = CommandPatterns.RETR_PATTERN.matcher(command);
      if (matcher.matches()) {
        fileName = matcher.group(1);
      }
      this.dataSocket = dataSocket;
      this.db = db;
    }

    @Override
    public String execute() {
      Path fileFullPath = status.getAbsoluteResolvedPath(Paths.get(fileName));
      if (!Files.exists(fileFullPath) || !existsInDatabase(fileFullPath)) {
        return FtpResponses.NO_SUCH_FILE_OR_DIRECTORY_550;
      }
      if (Files.isDirectory(fileFullPath)) {
        return FtpResponses.FILE_IS_A_DIRECTORY_550;
      }
      Path relativePath = status.getFtpRelativePath(fileFullPath);
      if (!correctPermissions(relativePath)) {
        return FtpResponses.PERMISSION_DENIED_550;
      }
      if (dataSocket.isActiveModeSet()) {
        try {
          dataSocket.connectActive();
        } catch (IOException e1) {
          return FtpResponses.CANT_OPEN_DATA_CONNECTION_425;
        }
      }
      StoppableTask task = new SendFileTask(status, dataSocket, fileFullPath);
      dataSocket.startThread(task);
      return String.format(FtpResponses.OPENING_BINARY_CONNECTION_150, fileName);
    }

    private boolean existsInDatabase(Path fileFullPath) {
      FileInfo info = null;
      try (Connection connection = Registry.createConnection()) {
        String relativePath = status.getFtpRelativePath(fileFullPath).toString();
        info = db.getFileInfo(connection, relativePath);
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return info != null;
    }

    private boolean correctPermissions(Path relativePath) {
      Database db = Database.getInstance();
      try (Connection connection = Registry.createConnection()) {
        int userId = db.getUserId(connection, status.getUsername());
        return db.canRead(connection, userId, relativePath.toString());
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return false;
    }

  }

  private static class RetrCommandLoggedInNoSocket extends RetrCommand {
    private RetrCommandLoggedInNoSocket() {}

    @Override
    public String execute() {
      return FtpResponses.USE_PORT_OR_PASV_425;
    }
  }

}
