package pl.edu.agh.kis.clientserverftp.client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.Timer;

import pl.edu.agh.kis.clientserverftp.client.view.MessageType;
import pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns;

/**
 * Handles server socket.
 * @author Wojciech Kumoń
 *
 */
public final class ServerSocketHandler {
  private ClientController controller;
  private Socket server;
  private PrintStream socketOut;
  private Scanner socketIn;
  private Timer noopTimer;


  /**
   * Constructs socket handler.
   * @param controller client controller
   */
  public ServerSocketHandler(ClientController controller) {
    this.controller = controller;
    // after 45sec from last contact with sever, noop will be sent
    noopTimer = new Timer(45000, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ServerSocketHandler.this.send(FtpClientCommands.getNoopCommand());
        ServerSocketHandler.this.nextInputLine();
      }
    });
    noopTimer.setRepeats(true);
  }

  /**
   * Tries to connect with server
   * @param loginData login data
   * @return true if connection was successful, false otherwise
   */
  public synchronized boolean connect(LoginData loginData) {
    server = new Socket();
    try {
      controller.printMessage("Trying to establish connection with " + loginData.getHost() + "...",
          MessageType.INFO);
      server.connect(new InetSocketAddress(loginData.getHost(), loginData.getPort()));
    } catch (IOException e) {
      controller.showError("Can't connect with " + loginData.getHost());
      controller.printMessage("Couldn't connect.", MessageType.INFO);
      server = null;
      return false;
    }
    controller.printMessage("Connected. Trying to log in...", MessageType.INFO);
    try {
      getIOStreams();
    } catch (IOException e) {
      controller.showError("Can't get server IO streams!");
      controller.printMessage("Couldn't connect.", MessageType.INFO);
      server = null;
      return false;
    }

    if (logIn(loginData)) {
      noopTimer.start();
      return true;
    } else {
      controller.printMessage("Not connected. Wrong login data.", MessageType.INFO);
      server = null;
      return false;
    }
  }

  private synchronized void getIOStreams() throws IOException {
    socketIn = new Scanner(new BufferedInputStream(server.getInputStream()));
    socketOut = new PrintStream(new BufferedOutputStream(server.getOutputStream()), true);
    nextInputLine();
  }

  private synchronized boolean logIn(LoginData loginData) {
    send(FtpClientCommands.getUserCommand(loginData.getUsername()));
    String response = nextInputLine();
    if (!(response.startsWith("3") || response.startsWith("2"))) {
      return false;
    }
    send(FtpClientCommands.getPassCommand(loginData.getPassword()));
    if (nextInputLine().startsWith("2")) {
      return true;
    }
    send(FtpClientCommands.getQuitCommand());
    nextInputLine();
    server = null;
    return false;
  }

  /**
   * Send message to server
   * @param line message to server
   */
  public synchronized void send(String line) {
    noopTimer.restart();
    if (CommandPatterns.PASS_PATTERN.matcher(line).matches()) {
      controller.printMessage("PASS ***", MessageType.INPUT);
    } else {
      controller.printMessage(line, MessageType.INPUT);
    }
    socketOut.println(line);
  }

  /**
   * Reads line from server
   * @return line from server
   */
  public synchronized String nextInputLine() {
    String inputLine = socketIn.nextLine();
    controller.printMessage(inputLine, MessageType.RESPONSE);
    return inputLine;
  }

  /**
   * Disconnect with server
   */
  public synchronized void disconnect() {
    noopTimer.stop();
    if (server != null && server.isConnected() && !server.isClosed()) {
      try {
        send(FtpClientCommands.getQuitCommand());
        nextInputLine();
        server.close();
      } catch (Exception e) {
        System.out.println("disconnected with exception");
      }
    }
  }

}
