package pl.edu.agh.kis.clientserverftp.server.commands;

import java.io.IOException;

import pl.edu.agh.kis.clientserverftp.server.ClientStatus;
import pl.edu.agh.kis.clientserverftp.server.DataSocket;
import pl.edu.agh.kis.clientserverftp.server.FtpResponses;
import pl.edu.agh.kis.clientserverftp.server.LoginStatus;

/**
 * FTP LIST command
 * @author Wojciech Kumoń
 *
 */
public class ListCommand implements Command {
  private ListCommand() {}

  /**
   * LIST command factory method
   * @param status client status
   * @param dataSocket data socket
   * @return new instance of LIST command
   */
  public static ListCommand newInstance(ClientStatus status, DataSocket dataSocket) {
    if (status.getLoginStatus() == LoginStatus.NOT_LOGGED_IN) {
      return new ListCommand();
    }
    if (dataSocket.isDataConnectionSet()) {
      return new ListCommandLoggedIn(dataSocket, status);
    }
    return new ListCommandLoggedInNoSocket();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.BAD_SEQUENCE_503;
  }

  private static class ListCommandLoggedIn extends ListCommand {
    private DataSocket dataSocket;
    private ClientStatus status;

    private ListCommandLoggedIn(DataSocket dataSocket, ClientStatus status) {
      this.dataSocket = dataSocket;
      this.status = status;
    }

    @Override
    public String execute() {
      if (dataSocket.isActiveModeSet()) {
        try {
          dataSocket.connectActive();
        } catch (IOException e1) {
          return FtpResponses.CANT_OPEN_DATA_CONNECTION_425;
        }
      }
      
      StoppableTask task = new ListTask(status, dataSocket);
      dataSocket.startThread(task);
      return FtpResponses.ASCII_CONNECTION_LS_150;
    }

  }

  private static class ListCommandLoggedInNoSocket extends ListCommand {
    private ListCommandLoggedInNoSocket() {}

    @Override
    public String execute() {
      return FtpResponses.USE_PORT_OR_PASV_425;
    }
  }

}
