package pl.edu.agh.kis.clientserverftp.server.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;

import pl.edu.agh.kis.clientserverftp.server.ClientStatus;
import pl.edu.agh.kis.clientserverftp.server.FtpResponses;
import pl.edu.agh.kis.clientserverftp.server.LoginStatus;
import pl.edu.agh.kis.clientserverftp.server.database.Database;
import pl.edu.agh.kis.clientserverftp.server.database.Registry;

/**
 * FTP RMD command
 * @author Wojciech Kumoń
 *
 */
public class RmdCommand implements Command {
  private RmdCommand() {}

  /**
   * RMD command factory method
   * @param status client status
   * @param command full command
   * @param database database
   * @return new instance of RMD command
   */
  public static RmdCommand newInstance(ClientStatus status, String command, Database database) {
    if (status.getLoginStatus() == LoginStatus.NOT_LOGGED_IN) {
      return new RmdCommand();
    }
    return new RmdCommandLoggedIn(status, command, database);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.BAD_SEQUENCE_503;
  }

  private static class RmdCommandLoggedIn extends RmdCommand {
    private ClientStatus status;
    private String dirName = "";
    private Database db;

    private RmdCommandLoggedIn(ClientStatus status, String command, Database database) {
      this.status = status;
      Matcher matcher = CommandPatterns.RMD_PATTERN.matcher(command);
      if (matcher.matches()) {
        dirName = matcher.group(1);
      }
      db = database;
    }

    @Override
    public String execute() {
      Path dirFullPath = status.getAbsoluteResolvedPath(Paths.get(dirName));
      Path relativePath = status.getFtpRelativePath(dirFullPath);
      if (!fileExists(dirFullPath, relativePath)) {
        return FtpResponses.NO_SUCH_FILE_OR_DIRECTORY_550;
      }
      if (!Files.isDirectory(dirFullPath)) {
        return String.format(FtpResponses.NOT_A_DIRECTORY_550, dirName);
      }
      if (!correctPermissions(dirFullPath, relativePath)) {
        return FtpResponses.PERMISSION_DENIED_550;
      }
      try {
        Files.delete(dirFullPath);
        deleteFromDatabase(dirFullPath);
      } catch (IOException e) {
        return String.format(FtpResponses.DIRECTORY_NOT_EMPTY_550, dirName);
      }
      return FtpResponses.RMD_SUCCESFUL_250;
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

    private void deleteFromDatabase(Path dirFullPath) {
      try (Connection connection = Registry.createConnection()) {
        String relativePath = status.getFtpRelativePath(dirFullPath).toString();
        db.deleteFile(connection, relativePath);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

  }

}
