package pl.edu.agh.kis.clientserverftp.server;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * OutputStream mediator.
 * @author Wojciech Kumoń
 *
 */
public class ClientOut implements Closeable {
  private PrintWriter clientOut;

  /**
   * Construct object
   * @param out OutputStream to handle
   */
  public ClientOut(OutputStream out) {
    clientOut = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(out)), true);
  }
  
  /**
   * Println to OutputStream
   * @param x message
   */
  public synchronized void println(String x) {
    clientOut.println(x);
  }

  /**
   * Method will close OutputSteam.
   */
  @Override
  public void close() {
    clientOut.close();
  }
  
}
