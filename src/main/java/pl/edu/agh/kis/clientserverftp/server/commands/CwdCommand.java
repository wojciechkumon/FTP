package pl.edu.agh.kis.clientserverftp.server.commands;

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
import pl.edu.agh.kis.clientserverftp.server.database.FileInfo;
import pl.edu.agh.kis.clientserverftp.server.database.Registry;

/**
 * FTP CWD command
 * @author Wojciech Kumoń
 *
 */
public class CwdCommand implements Command {
  private CwdCommand() {}

  /**
   * CWD command factory method
   * @param status client status
   * @param command full command
   * @param db database
   * @return new instance of CWD command
   */
  public static CwdCommand newInstance(ClientStatus status, String command, Database db) {
    if (status.getLoginStatus() == LoginStatus.NOT_LOGGED_IN) {
      return new CwdCommand();
    }
    return new CwdLoggedInCommand(status, command, db);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.BAD_SEQUENCE_503;
  }

  private static class CwdLoggedInCommand extends CwdCommand {
    private ClientStatus status;
    private String arg = "";
    private Database db;

    private CwdLoggedInCommand(ClientStatus status, String command, Database db) {
      this.status = status;
      Matcher matcher = CommandPatterns.CWD_PATTERN.matcher(command);
      if (matcher.matches()) {
        arg = matcher.group(1);
      }
      this.db = db;
    }

    @Override
    public String execute() {
      Path newPath = status.getAbsoluteResolvedPath(Paths.get(arg));
      Path relativePath = status.getFtpRelativePath(newPath);
      if (Files.exists(newPath) && Files.isDirectory(newPath) && existsInDb(relativePath)) {
        status.changeDirectory(relativePath.toString());
        return FtpResponses.CWD_SUCCESFUL_250;
      }
      return FtpResponses.NO_SUCH_FILE_OR_DIRECTORY_550;
    }

    private boolean existsInDb(Path relativePath) {
      FileInfo info = null;
      try (Connection connection = Registry.createConnection()) {
        info = db.getFileInfo(connection, relativePath.toString());
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return info != null;
    }

  }

}
