package pl.edu.agh.kis.clientserverftp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Allows launching server
 * @author Wojciech Kumoń
 */
public class Server implements Runnable {
  /**
   * Default command port.
   */
  public static short COMMAND_PORT = 21;
  
  /**
   * Default data port.
   */
  public static short DATA_PORT = 20;
  
  /**
   * Start of server ip.
   */
  public static String SERVER_IP_START = "127";

  /**
   * Method will run a server.
   */
  @Override
  public void run() {
    try (ServerSocket server = new ServerSocket(COMMAND_PORT)) {
      int threads = ServerProperties.getThreadPoolSize();
      ExecutorService executor = Executors.newFixedThreadPool(threads);
      printStartInfo();
      while (true) {
        try {
          Socket socket = server.accept();
          executor.execute(new ClientSocketHandler(socket));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void printStartInfo() {
    try {
      System.out.println("--------------------------------");
      System.out
          .println("ROOT PATH: " + Paths.get(ServerProperties.getRootDirectoryPath()).toRealPath());
      System.out.println("THREAD POOL SIZE: " + ServerProperties.getThreadPoolSize());
      System.out.println("--------------------------------");
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("--------- SERVER START ---------");
  }

}
