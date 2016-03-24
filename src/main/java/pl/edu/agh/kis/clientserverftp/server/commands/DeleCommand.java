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
 * FTP DELE command
 * @author Wojciech Kumoń
 *
 */
public class DeleCommand implements Command {
  private DeleCommand() {}

  /**
   * DELE command factory method
   * @param status client status
   * @param command full command
   * @param database database
   * @return new instance of DELE command
   */
  public static DeleCommand newInstance(ClientStatus status, String command, Database database) {
    if (status.getLoginStatus() == LoginStatus.NOT_LOGGED_IN) {
      return new DeleCommand();
    }
    return new DeleCommandLoggedIn(status, command, database);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.BAD_SEQUENCE_503;
  }


  private static class DeleCommandLoggedIn extends DeleCommand {
    private ClientStatus status;
    private String fileToRemove = "";
    private Database db;

    private DeleCommandLoggedIn(ClientStatus status, String command, Database database) {
      this.status = status;
      Matcher matcher = CommandPatterns.DELE_PATTERN.matcher(command);
      if (matcher.matches()) {
        fileToRemove = matcher.group(1);
      }
      db = database;
    }

    @Override
    public String execute() {
      Path fileFullPath = status.getAbsoluteResolvedPath(Paths.get(fileToRemove));
      Path relativePath = status.getFtpRelativePath(fileFullPath);
      if (!fileExists(fileFullPath, relativePath)) {
        return FtpResponses.NO_SUCH_FILE_OR_DIRECTORY_550;
      }
      if (Files.isDirectory(fileFullPath)) {
        return String.format(FtpResponses.FILE_IS_A_DIRECTORY_550, fileToRemove);
      }
      if (!correctPermissions(fileFullPath, relativePath)) {
        return FtpResponses.PERMISSION_DENIED_550;
      }
      try {
        Files.delete(fileFullPath);
        deleteFromDatabase(fileFullPath);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return FtpResponses.DELE_SUCCESFUL_250;
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

    private void deleteFromDatabase(Path fileFullPath) {
      try (Connection connection = Registry.createConnection()) {
        String relativePath = status.getFtpRelativePath(fileFullPath).toString();
        db.deleteFile(connection, relativePath);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

  }


}
