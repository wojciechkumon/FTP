package pl.edu.agh.kis.clientserverftp.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import pl.edu.agh.kis.clientserverftp.server.commands.CommandExecutor;
import pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns;

/**
 * Handles one client.
 * @author Wojciech Kumoń
 */
public class ClientSocketHandler implements Runnable {
  private static long TIMEOUT = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);

  private DataSocket dataSocket = new DataSocket();
  private ClientStatus status;
  private Socket clientSocket;
  private Scanner clientIn;
  private ClientOut clientOut;

  /**
   * Constructs client socket handler.
   * @param socket connected socket
   * @throws IOException IOException can be thrown while getting i/o streams
   */
  public ClientSocketHandler(Socket socket) throws IOException {
    clientSocket = socket;
    clientIn = new Scanner(new BufferedInputStream(socket.getInputStream()));
    clientOut = new ClientOut(socket.getOutputStream());
    status = new ClientStatus(clientOut);
  }

  /**
   * Starts handling client socket.
   */
  @Override
  public void run() {
    try {
      handleSocket();
    } catch (SocketException e) {
      e.printStackTrace();
    } finally {
      closeAll();
    }
  }

  private void handleSocket() throws SocketException {
    clientSocket.setSoTimeout((int) TIMEOUT);
    String clientIp = clientSocket.getInetAddress().getHostAddress();
    status.log("Connected with: " + clientIp);
    printWelcomeMessage();
    CommandExecutor commandExecutor = new CommandExecutor(status, dataSocket);
    while ((!Thread.currentThread().isInterrupted()) && clientIn.hasNext()) {
      String inputLine = clientIn.nextLine();
      String command = getUpperCaseCommand(inputLine);
      logUserCommand(command);
      String response = commandExecutor.executeCommand(command);
      if (!response.isEmpty()) {
        status.log("RESPONSE: " + response);
        clientOut.println(response);
      }
      if (response.equals(FtpResponses.BYE_221)) {
        break;
      }
    }
  }

  private void logUserCommand(String command) {
    if (CommandPatterns.PASS_PATTERN.matcher(command).matches()) {
      status.log("> PASS ****");
    } else {
      status.log("> " + command);
    }
  }

  private void printWelcomeMessage() {
    clientOut.println(FtpResponses.WELCOME_220);
  }

  private String getUpperCaseCommand(String command) {
    String[] splitted = command.split(" ");
    StringBuilder builder = new StringBuilder(splitted[0].toUpperCase());
    for (int i = 1; i < splitted.length; ++i) {
      builder.append(" " + splitted[i]);
    }
    return builder.toString();
  }

  private void closeAll() {
    try {
      clientOut.close();
      dataSocket.close();
      clientSocket.close();
      if (status != null) {
        status.closeLogger();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
