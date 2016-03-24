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
 * FTP MKD command
 * @author Wojciech Kumoń
 *
 */
public class MkdCommand implements Command {
  private MkdCommand() {}

  /**
   * MKD command factory method
   * @param status client status
   * @param command full command
   * @param db database
   * @return new instance of MKD command
   */
  public static MkdCommand newInstance(ClientStatus status, String command, Database db) {
    if (status.getLoginStatus() == LoginStatus.NOT_LOGGED_IN) {
      return new MkdCommand();
    }
    return new MkdCommandLoggedIn(status, command, db);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.BAD_SEQUENCE_503;
  }

  private static class MkdCommandLoggedIn extends MkdCommand {
    private ClientStatus status;
    private String newDirName = "";
    private Database db;

    private MkdCommandLoggedIn(ClientStatus status, String command, Database database) {
      this.status = status;
      Matcher matcher = CommandPatterns.MKD_PATTERN.matcher(command);
      if (matcher.matches()) {
        newDirName = matcher.group(1);
      }
      db = database;
    }

    @Override
    public String execute() {
      Path dirFullPath = status.getAbsoluteResolvedPath(Paths.get(newDirName));
      Path relativePath = status.getFtpRelativePath(dirFullPath);
      if (fileExists(dirFullPath, relativePath)) {
        return String.format(FtpResponses.FILE_EXIST_550, newDirName);
      }
      if (!correctPermissions(status.getClientPath())) {
        return FtpResponses.PERMISSION_DENIED_550;
      }
      try {
        Files.createDirectory(dirFullPath);
        addToDatabase(dirFullPath);
      } catch (IOException e) {
        return String.format(FtpResponses.FILE_EXIST_550, newDirName);
      }
      return FtpResponses.DIR_CREATED_257;
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

    private boolean correctPermissions(Path relativePath) {
      if (relativePath.toString().isEmpty()) {
        return true;
      }
      Database db = Database.getInstance();
      try (Connection connection = Registry.createConnection()) {
        int userId = db.getUserId(connection, status.getUsername());
        return db.canRead(connection, userId, relativePath.toString());
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return false;
    }

    private void addToDatabase(Path dirFullPath) {
      try (Connection connection = Registry.createConnection()) {
        String relativePath = status.getFtpRelativePath(dirFullPath).toString();
        db.addFile(connection, relativePath, status.getUsername());
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

  }

}
