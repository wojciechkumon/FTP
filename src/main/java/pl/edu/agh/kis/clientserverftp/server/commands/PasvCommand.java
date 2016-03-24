package pl.edu.agh.kis.clientserverftp.server.commands;

import pl.edu.agh.kis.clientserverftp.server.ClientStatus;
import pl.edu.agh.kis.clientserverftp.server.DataSocket;
import pl.edu.agh.kis.clientserverftp.server.FtpResponses;
import pl.edu.agh.kis.clientserverftp.server.LoginStatus;

/**
 * FTP PASV command
 * @author Wojciech Kumoń
 *
 */
public class PasvCommand implements Command {
  private PasvCommand() {}

  /**
   * PASV command factory method
   * @param status client status
   * @param dataSocket data socket
   * @return new instance of PASV command
   */
  public static PasvCommand newInstance(ClientStatus status, DataSocket dataSocket) {
    if (status.getLoginStatus() == LoginStatus.NOT_LOGGED_IN) {
      return new PasvCommand();
    }
    return new PasvCommandLoggedIn(status, dataSocket);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.BAD_SEQUENCE_503;
  }

  private static class PasvCommandLoggedIn extends PasvCommand {
    private ClientStatus status;
    private DataSocket dataSocket;

    public PasvCommandLoggedIn(ClientStatus status, DataSocket dataSocket) {
      this.status = status;
      this.dataSocket = dataSocket;
    }

    @Override
    public String execute() {
      dataSocket.startPassivePortListening(status);
      return "";
    }
  }

}
