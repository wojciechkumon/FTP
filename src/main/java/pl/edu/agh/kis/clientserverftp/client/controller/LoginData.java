package pl.edu.agh.kis.clientserverftp.client.controller;

/**
 * Hold login data, immutable.
 * @author Wojciech Kumoń
 *
 */
public class LoginData {
  private final String host;
  private final String username;
  private final String password;
  private final int port;

  /**
   * Constructs login data
   * @param host host
   * @param username username
   * @param password password
   * @param port port
   */
  public LoginData(String host, String username, String password, int port) {
    this.host = host;
    this.username = username;
    this.password = password;
    this.port = port;
  }

  /**
   * Returns host
   * @return host
   */
  public String getHost() {
    return host;
  }

  /**
   * Returns username
   * @return username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Return password
   * @return password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Returns port
   * @return port
   */
  public int getPort() {
    return port;
  }

}
