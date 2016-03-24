package pl.edu.agh.kis.clientserverftp.server;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.edu.agh.kis.clientserverftp.server.commands.StoppableTask;

/**
 * Class representing data socket.
 * 
 * @author Wojciech Kumoń
 */
public class DataSocket implements Closeable {
  private DataConnectionType connectionType = DataConnectionType.NONE;
  private Socket socket = null;
  private InetSocketAddress activeAddress = null;
  private StoppableTask lastTask = null;
  private ExecutorService executor = Executors.newSingleThreadExecutor();
  private Thread passiveServer = null;

  /**
   * Returns data socket
   * 
   * @return data socket or null if not set
   */
  public synchronized Socket getSocket() {
    return socket;
  }

  /**
   * Sets inetAddress to connect in active mode
   * 
   * @param inetSocketAddress inet address
   */
  public synchronized void setActiveDataAddress(InetSocketAddress inetSocketAddress) {
    this.activeAddress = inetSocketAddress;
    connectionType = DataConnectionType.ACTIVE;
  }

  /**
   * Checks if data connection type is set
   * 
   * @return true if data connection type is set, false otherwise
   */
  public synchronized boolean isDataConnectionSet() {
    return connectionType != DataConnectionType.NONE;
  }

  /**
   * Checks if active mode is set
   * 
   * @return true if active mode is set, false otherwise
   */
  public synchronized boolean isActiveModeSet() {
    return connectionType == DataConnectionType.ACTIVE;
  }

  /**
   * Connects in active mode. First set active data address.
   * 
   * @throws IOException IOException may be thrown while connecting
   */
  public synchronized void connectActive() throws IOException {
    socket = new Socket();
    socket.connect(activeAddress);
    if (passiveServer != null) {
      passiveServer.interrupt();
      passiveServer = null;
    }
  }

  /**
   * Stops current task operating on socket. If there is no running task, method won't have effect.
   */
  public synchronized void stop() {
    if (lastTask != null) {
      lastTask.stop();
    }
  }

  /**
   * Starts new thread with sent stoppable task
   * @param task task to execute
   */
  public synchronized void startThread(StoppableTask task) {
    stop();
    executor.execute(task);
    lastTask = task;
  }

  /**
   * Sets passive mode socket.
   * @param socket socket to set
   */
  public synchronized void setPassiveSocket(Socket socket) {
    this.socket = socket;
    connectionType = DataConnectionType.PASSIVE;
  }

  /**
   * Starts listening in passive mode for client
   * @param status client status
   */
  public synchronized void startPassivePortListening(ClientStatus status) {
    passiveServer = new Thread(new PassiveServer(status, this));
    passiveServer.start();
  }

  /**
   * Closes data socket.
   */
  @Override
  public synchronized void close() {
    if (socket != null) {
      try {
        stop();
        executor.shutdown();
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
