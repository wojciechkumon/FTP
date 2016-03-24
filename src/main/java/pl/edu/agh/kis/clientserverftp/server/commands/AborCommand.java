package pl.edu.agh.kis.clientserverftp.server.commands;

import pl.edu.agh.kis.clientserverftp.server.DataSocket;
import pl.edu.agh.kis.clientserverftp.server.FtpResponses;
import pl.edu.agh.kis.clientserverftp.server.LoginStatus;

/**
 * FTP ABOR command
 * @author Wojciech Kumoń
 *
 */
public class AborCommand implements Command {
  private AborCommand() {}

  /**
   * Abor command factory method
   * @param loginStatus client login status
   * @param dataSocket client data socket
   * @return new instance of abor command
   */
  public static AborCommand newInstance(LoginStatus loginStatus, DataSocket dataSocket) {
    if (loginStatus == LoginStatus.NOT_LOGGED_IN) {
      return new AborCommand();
    }
    return new AborCommandLoggedIn(dataSocket);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.BAD_SEQUENCE_503;
  }
  
  private static class AborCommandLoggedIn extends AborCommand {
    private DataSocket dataSocket;

    public AborCommandLoggedIn(DataSocket dataSocket) {
      this.dataSocket = dataSocket;
    }
    
    @Override
    public String execute() {
      dataSocket.stop();
      return FtpResponses.ABORT_SUCCESSFUL_226;
    }
    
  }

}
