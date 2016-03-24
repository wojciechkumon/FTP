package pl.edu.agh.kis.clientserverftp.server;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Represents status of single client, uses file logger.
 * @author Wojciech Kumoń
 *
 */
public class ClientStatus {
  private ClientPath path = new ClientPath();
  private String lastCommand = "";
  private LoginStatus login = LoginStatus.NOT_LOGGED_IN;
  private String username = "";
  private final ClientOut clientOut;
  private final ServerLogger logger;

  /**
   * Construct status
   * @param clientOut ClientOut which allows sending message to client
   */
  public ClientStatus(ClientOut clientOut) {
    this.clientOut = clientOut;
    logger = new ServerLogger();
  }

  /**
   * Changes current directory.
   * @param dir new directory
   */
  public void changeDirectory(String dir) {
    path.changeDirectory(dir);
  }

  /**
   * Return current client path.
   * @return current client path
   */
  public Path getClientPath() {
    return path.getClientPath();
  }

  /**
   * Return current server real path.
   * @return current real server path
   */
  public Path getRealPath() {
    return path.getRealPath();
  }

  /**
   * Returns resolved absolute server path.
   * @param pathToResolve path to resolve
   * @return resolved absolute server path
   */
  public Path getAbsoluteResolvedPath(Path pathToResolve) {
    return path.getAbsoluteResolvedPath(pathToResolve);
  }
  
  /**
   * Returns relativised server path.
   * 
   * @param pathToRelativize path to relativise
   * @return relativised server path
   */
  public Path getFtpRelativePath(Path pathToRelativize) {
    return ClientPath.getFtpRelativePath(pathToRelativize);
  }

  /**
   * Returns last client command
   * @return last client command
   */
  public String getLastCommand() {
    return lastCommand;
  }

  /**
   * Sets last client command
   * @param lastCommand command to set
   */
  public void setLastCommand(String lastCommand) {
    this.lastCommand = lastCommand;
  }

  /**
   * Returns current client login status
   * @return current client login status
   */
  public LoginStatus getLoginStatus() {
    return login;
  }

  /**
   * Sets if client is logged in
   * @param loginStatus login status
   */
  public void setLoggedIn(LoginStatus loginStatus) {
    this.login = loginStatus;
  }

  /**
   * Returns client username.
   * @return client username if logged in, empty string otherwise
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets logged in client username
   * @param username username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Return client out
   * @return client out
   */
  public ClientOut getClientOut() {
    return clientOut;
  }
  
  /**
   * Logs message
   * @param x message to log
   */
  public void log(String x) {
    logger.log(x);
  }
  
  /**
   * Method closing logger
   * @throws IOException IOException may be thrown while closing logger
   */
  public void closeLogger() throws IOException {
    logger.close();
  }

}
