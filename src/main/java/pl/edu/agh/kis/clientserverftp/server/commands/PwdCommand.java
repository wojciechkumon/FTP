package pl.edu.agh.kis.clientserverftp.server.commands;

import java.nio.file.Path;

import pl.edu.agh.kis.clientserverftp.server.ClientStatus;
import pl.edu.agh.kis.clientserverftp.server.FtpResponses;
import pl.edu.agh.kis.clientserverftp.server.LoginStatus;

/**
 * FTP PWD command
 * @author Wojciech Kumoń
 *
 */
public class PwdCommand implements Command {
  private PwdCommand() {}
  
  /**
   * PWD command factory method
   * @param status client status
   * @return new instance of PWD command
   */
  public static PwdCommand newInstance(ClientStatus status) {
    if (status.getLoginStatus() == LoginStatus.LOGGED_IN) {
      return new PwdCommandLoggedIn(status.getClientPath());
    }
    return new PwdCommand();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.BAD_SEQUENCE_503;
  }
  
  private static class PwdCommandLoggedIn extends PwdCommand {
    private Path path;
    
    private PwdCommandLoggedIn(Path path) {
      this.path = path;
    }

    @Override
    public String execute() {
      return String.format(FtpResponses.CURRENT_DIR_257, "/" + path.toString());
    }
  }

}
