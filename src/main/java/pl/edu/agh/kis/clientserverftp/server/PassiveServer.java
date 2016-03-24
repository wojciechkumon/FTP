package pl.edu.agh.kis.clientserverftp.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * This class allows using passive mode by creating passive server
 * @author Wojciech Kumoń
 *
 */
public class PassiveServer implements Runnable {
  private static int ANY_FREE_PORT = 0;
  private ClientStatus status;
  private DataSocket dataSocket;

  /**
   * Constructs object.
   * @param status client status
   * @param dataSocket dataSocket to set if client will be connected
   */
  public PassiveServer(ClientStatus status, DataSocket dataSocket) {
    this.status = status;
    this.dataSocket = dataSocket;
  }

  /**
   * Starts passive server.
   */
  @Override
  public void run() {
    try (ServerSocket server = new ServerSocket(ANY_FREE_PORT)) {
      int porth = server.getLocalPort() / 256;
      int portl = server.getLocalPort() % 256;
      String serverIp = getServerIp();
      if (!serverIp.isEmpty()) {
        String passiveAddress = getConnectionString(serverIp, porth, portl);
        sendResponse(String.format(FtpResponses.ENTERING_PASSIVE_MODE_227, passiveAddress));
      } else {
        sendResponse(FtpResponses.CANT_OPEN_PASSIVE_PORT_425);
      }
      dataSocket.setPassiveSocket(server.accept());
    } catch (IOException e) {
      sendResponse(FtpResponses.CANT_OPEN_PASSIVE_PORT_425);
    }
  }

  private String getServerIp() throws SocketException {
    Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
    while (netInterfaces.hasMoreElements()) {
      NetworkInterface netInter = netInterfaces.nextElement();
      Enumeration<InetAddress> addresses = netInter.getInetAddresses();
      while (addresses.hasMoreElements()) {
        InetAddress currentAddress = addresses.nextElement();
        if (currentAddress.getHostAddress().startsWith(Server.SERVER_IP_START)) {
          return currentAddress.getHostAddress();
        }
      }
    }
    return "";
  }

  private String getConnectionString(String serverIp, int porth, int portl) {
    StringBuilder builder = new StringBuilder(23);
    builder.append(serverIp.replaceAll("\\.", ","));
    builder.append(",");
    builder.append(porth);
    builder.append(",");
    builder.append(portl);
    return builder.toString();
  }

  private void sendResponse(String response) {
    status.getClientOut().println(response);
    status.log("RESPONSE: " + response);
  }

}
