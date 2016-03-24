package pl.edu.agh.kis.clientserverftp.server.commands;

import java.util.regex.Matcher;

import pl.edu.agh.kis.clientserverftp.server.FtpResponses;
import pl.edu.agh.kis.clientserverftp.server.LoginStatus;

/**
 * FTP USER command
 * @author Wojciech Kumoń
 *
 */
public class UserCommand implements Command {
  private UserCommand() {}

  /**
   * USER command factory method
   * @param login client login status
   * @param command full command
   * @return new instance of USER command
   */
  public static UserCommand newInstance(LoginStatus login, String command) {
    if (login == LoginStatus.NOT_LOGGED_IN) {
      return new UserCommandNotLoggedIn(command);
    }
    return new UserCommand();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.YOU_ARE_LOGGED_IN_503;
  }


  private static class UserCommandNotLoggedIn extends UserCommand {
    private String username = "";

    private UserCommandNotLoggedIn(String command) {
      Matcher matcher = CommandPatterns.USER_PATTERN.matcher(command);
      if (matcher.matches()) {
        username = matcher.group(1);
      }
    }

    @Override
    public String execute() {
      return String.format(FtpResponses.PASSWORD_REQUIRED_331, username);
    }

  }

}
