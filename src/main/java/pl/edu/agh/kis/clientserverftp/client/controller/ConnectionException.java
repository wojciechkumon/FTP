package pl.edu.agh.kis.clientserverftp.client.controller;

/**
 * Connection exception.
 * @author Wojciech Kumoń
 *
 */
public class ConnectionException extends Exception {
  private static final long serialVersionUID = 6895444247851642088L;

  /**
   * Constructs exception;
   */
  public ConnectionException() {}

  /**
   * Constructs exception with cause
   * @param cause exception cause
   */
  public ConnectionException(Throwable cause) {
    super(cause);
  }
  
}
