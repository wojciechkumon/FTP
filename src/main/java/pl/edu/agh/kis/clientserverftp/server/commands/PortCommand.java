package pl.edu.agh.kis.clientserverftp.server.commands;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.edu.agh.kis.clientserverftp.server.DataSocket;
import pl.edu.agh.kis.clientserverftp.server.FtpResponses;
import pl.edu.agh.kis.clientserverftp.server.LoginStatus;

/**
 * FTP PORT command
 * 
 * @author Wojciech Kumoń
 *
 */
public class PortCommand implements Command {
  private PortCommand() {}

  /**
   * PORT command factory method
   * 
   * @param loginStatus client login status
   * @param command full command
   * @param dataSocket data socket
   * @return new instance of PORT command
   */
  public static PortCommand newInstance(LoginStatus loginStatus, String command,
      DataSocket dataSocket) {
    if (loginStatus == LoginStatus.LOGGED_IN) {
      return new PortCommandLoggedIn(command, dataSocket);
    }
    return new PortCommand();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.BAD_SEQUENCE_503;
  }

  private static class PortCommandLoggedIn extends PortCommand {
    private static final String NUMBER_0_TO_255_REGEX = "\\b(1?[0-9]{1,2}|2[0-4][0-9]|25[0-5])\\b";
    private static final Pattern CORRECT_PORT_IP_PATTERN;
    private String command;
    private DataSocket dataSocket;

    static {
      StringBuilder builder = new StringBuilder("PORT " + NUMBER_0_TO_255_REGEX);
      for (int i = 0; i < 5; ++i) {
        builder.append("," + NUMBER_0_TO_255_REGEX);
      }
      CORRECT_PORT_IP_PATTERN = Pattern.compile(builder.toString());
    }

    private PortCommandLoggedIn(String command, DataSocket dataSocket) {
      this.command = command;
      this.dataSocket = dataSocket;
    }

    @Override
    public String execute() {
      Matcher matcher = CORRECT_PORT_IP_PATTERN.matcher(command);
      if (!matcher.matches()) {
        return ArgumentSyntaxErrorCommand.newInstance().execute();
      }
      int port = getPort(matcher);
      String ip = getIp(matcher);
      try {
        InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(ip), port);
        dataSocket.setActiveDataAddress(address);
        return FtpResponses.COMMAND_SUCCESFUL_200;
      } catch (UnknownHostException e) {
        return FtpResponses.HOST_NOT_AVAILABLE_434;
      }
    }

    private int getPort(Matcher matcher) {
      return getOctet(5, matcher) * 256 + getOctet(6, matcher);
    }

    private int getOctet(int numberOfOctet, Matcher matcher) {
      return Integer.parseInt(matcher.group(numberOfOctet));
    }

    private String getIp(Matcher matcher) {
      StringBuilder ipBuilder = new StringBuilder(Integer.toString(getOctet(1, matcher)));
      for (int i = 2; i <= 4; ++i) {
        ipBuilder.append("." + getOctet(i, matcher));
      }
      return ipBuilder.toString();
    }

  }

}
