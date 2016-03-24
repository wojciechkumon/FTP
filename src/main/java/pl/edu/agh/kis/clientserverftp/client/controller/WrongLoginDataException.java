package pl.edu.agh.kis.clientserverftp.client.controller;

/**
 * Wrong login data exception
 * @author Wojciech Kumoń
 *
 */
public class WrongLoginDataException extends Exception {
  private static final long serialVersionUID = 5954631126857403917L;

  /**
   * Exception constructor.
   * @param message exception message
   */
  public WrongLoginDataException(String message) {
    super(message);
  } 
  
  /**
   * Exception constructor with cause.
   * @param message exception message
   * @param cause exception cause
   */
  public WrongLoginDataException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
