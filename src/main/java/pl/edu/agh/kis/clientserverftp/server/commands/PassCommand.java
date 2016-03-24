package pl.edu.agh.kis.clientserverftp.server.commands;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;

import pl.edu.agh.kis.clientserverftp.server.ClientStatus;
import pl.edu.agh.kis.clientserverftp.server.FtpResponses;
import pl.edu.agh.kis.clientserverftp.server.LoginStatus;
import pl.edu.agh.kis.clientserverftp.server.database.Database;
import pl.edu.agh.kis.clientserverftp.server.database.Registry;

/**
 * FTP PASS command
 * @author Wojciech Kumoń
 *
 */
public class PassCommand implements Command {
  private PassCommand() {}

  /**
   * PASS command factory method
   * @param status client status
   * @param command full command
   * @param db database
   * @return new instance of PASS command
   */
  public static PassCommand newInstance(ClientStatus status, String command, Database db) {
    if (status.getLoginStatus() == LoginStatus.NOT_LOGGED_IN) {
      return new PasswordCommandNotLoggedIn(command, status, db);
    }
    return new PassCommand();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.YOU_ARE_LOGGED_IN_503;
  }


  private static class PasswordCommandNotLoggedIn extends PassCommand {
    private static String ANONYMOUS = "anonymous";
    private String username = "";
    private String password = "";
    private ClientStatus status;
    private Database db;

    private PasswordCommandNotLoggedIn(String command, ClientStatus status, Database db) {
      Matcher matcher = CommandPatterns.PASS_PATTERN.matcher(command);
      if (matcher.matches()) {
        password = matcher.group(1);
      }
      matcher = CommandPatterns.USER_PATTERN.matcher(status.getLastCommand());
      if (matcher.matches()) {
        username = matcher.group(1);
      }
      this.status = status;
      this.db = db;
    }

    @Override
    public String execute() {
      if (username.isEmpty()) {
        return FtpResponses.BAD_SEQUENCE_503;
      }
      if (username.equals(ANONYMOUS)) {
        return logIn();
      }
      try (Connection connection = Registry.createConnection()) {
        if (db.isPasswordCorrect(connection, username, password)) {
          return logIn();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return FtpResponses.WRONG_PASSWORD_430;
    }

    private String logIn() {
      status.setLoggedIn(LoginStatus.LOGGED_IN);
      status.setUsername(username);
      return String.format(FtpResponses.USER_LOGGED_IN_230, username);
    }

  }

}


