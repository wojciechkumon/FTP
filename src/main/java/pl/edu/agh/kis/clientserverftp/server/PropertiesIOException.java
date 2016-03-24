package pl.edu.agh.kis.clientserverftp.server;

/**
 * Properties exception
 * 
 * @author Wojciech Kumoń
 *
 */
public class PropertiesIOException extends RuntimeException {
  private static final long serialVersionUID = -1595977070272448808L;

  /**
   * Constructor with cause.
   * 
   * @param cause cause of exception
   */
  public PropertiesIOException(Throwable cause) {
    super(cause);
  }
}
