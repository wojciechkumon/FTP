package pl.edu.agh.kis.clientserverftp.server.commands;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;

import pl.edu.agh.kis.clientserverftp.server.ClientStatus;
import pl.edu.agh.kis.clientserverftp.server.FtpResponses;
import pl.edu.agh.kis.clientserverftp.server.LoginStatus;
import pl.edu.agh.kis.clientserverftp.server.database.Database;
import pl.edu.agh.kis.clientserverftp.server.database.FileInfo;
import pl.edu.agh.kis.clientserverftp.server.database.FileInfo.FileInfoBuilder;
import pl.edu.agh.kis.clientserverftp.server.database.Registry;

/**
 * FTP CHMOD command
 * 
 * @author Wojciech Kumoń
 *
 */
public class ChmodCommand implements Command {
  private ChmodCommand() {}

  /**
   * CHMOD command factory method
   * 
   * @param status client status
   * @param command full command
   * @param db database
   * @return new instance of CHMOD command
   */
  public static Command newInstance(ClientStatus status, String command, Database db) {
    if (status.getLoginStatus() == LoginStatus.NOT_LOGGED_IN) {
      return new ChmodCommand();
    }
    return new ChmodLoggedInCommand(status, command, db);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.BAD_SEQUENCE_503;
  }

  private static class ChmodLoggedInCommand extends ChmodCommand {
    private ClientStatus status;
    private Database db;
    private String filePath = "";
    private int userPermissions = -1;
    private int groupPermissions = -1;

    private ChmodLoggedInCommand(ClientStatus status, String command, Database db) {
      this.status = status;
      this.db = db;
      Matcher matcher = CommandPatterns.CHMOD_PATTERN.matcher(command);
      if (matcher.matches()) {
        filePath = matcher.group(1);
        userPermissions = Integer.parseInt(matcher.group(2));
        groupPermissions = Integer.parseInt(matcher.group(3));
      }
    }

    @Override
    public String execute() {
      if (userPermissions < 0) {
        return FtpResponses.SYNTAX_ERROR_500;
      }
      Path fileFullPath = status.getAbsoluteResolvedPath(Paths.get(filePath));
      Path relativePath = status.getFtpRelativePath(fileFullPath);
      FileInfo fileInfo = getFileInfo(relativePath.toString());
      try (Connection connection = Registry.createConnection()) {
        int userId = db.getUserId(connection, status.getUsername());
        if (!db.canWrite(connection, userId, relativePath.toString())) {
          return FtpResponses.PERMISSION_DENIED_550;
        }
        db.changePermissions(connection, fileInfo);
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return FtpResponses.CHMOD_SUCCESFUL_250;
    }

    private FileInfo getFileInfo(String relativePath) {
      FileInfoBuilder builder = new FileInfo.FileInfoBuilder().path(relativePath);
      if (userPermissions > 0) {
        builder.userPermissions(userPermissions % 2 == 1, userPermissions >= 2);
      }
      if (groupPermissions > 0) {
        builder.groupPermissions(groupPermissions % 2 == 1, groupPermissions >= 2);
      }
      return builder.build();
    }

  }

}
