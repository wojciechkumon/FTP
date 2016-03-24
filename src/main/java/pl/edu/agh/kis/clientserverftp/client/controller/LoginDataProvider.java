package pl.edu.agh.kis.clientserverftp.client.controller;

/**
 * Provides login data.
 * @author Wojciech Kumoń
 *
 */
public interface LoginDataProvider {
  /**
   * Returns host
   * @return host
   */
  String getHost();

  /**
   * Returns username
   * @return username
   */
  String getUsername();

  /**
   * Returns password
   * @return password
   */
  char[] getPassword();

  /**
   * Returns port
   * @return port
   */
  String getPort();
}
