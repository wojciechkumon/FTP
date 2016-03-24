package pl.edu.agh.kis.clientserverftp.client.controller;

import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import pl.edu.agh.kis.clientserverftp.client.view.FtpClient;
import pl.edu.agh.kis.clientserverftp.client.view.MessageType;
import pl.edu.agh.kis.clientserverftp.client.view.TextSource;

/**
 * FTP client controller.
 * @author Wojciech Kumoń
 *
 */
public class ClientController {
  private FtpClient client;
  private ServerSocketHandler serverHandler;
  private ServerMediator mediator = new ServerMediator(this);
  private FtpSystemModel ftpModel;
  private FileSystemModel localFilesModel = new FileSystemModel();

  /**
   * Constructs client controller
   * @param client client view
   */
  public ClientController(FtpClient client) {
    this.client = client;
    serverHandler = new ServerSocketHandler(this);
    client.addServerTreeMouseListener(new FtpFileTreeMouseListener(this));
    client.setController(this);
  }

  /**
   * Tries to connect with server
   * @param loginData login data
   */
  public void connect(LoginData loginData) {
    serverHandler.disconnect();
    if (serverHandler.connect(loginData)) {
      client.setEnabled(true);
      ftpModel = new FtpSystemModel(this);
      client.setServerFilesTreeModel(ftpModel);
      mediator.setBinaryMode();
    } else {
      client.setServerFilesTreeModel(new EmptyTreeModel());
      client.setEnabled(false);
    }
  }

  /**
   * Disconnects with server.
   */
  public void disconnect() {
    serverHandler.disconnect();
    client.setServerFilesTreeModel(new EmptyTreeModel());
    client.setEnabled(false);
  }
  
  /**
   * Clears ftp dirs cache.
   */
  public void clearCache() {
    ftpModel.clearCache();
    ftpModel = new FtpSystemModel(this);
    client.setServerFilesTreeModel(ftpModel);
  }

  /**
   * Prints message
   * @param message message to print
   * @param type message type
   */
  public void printMessage(String message, MessageType type) {
    client.addMessage(message, type);
  }

  /**
   * Show error dialog
   * @param message message to show in dialog
   */
  public void showError(String message) {
    JOptionPane.showMessageDialog(client, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Sends message to server
   * @param message message to send
   */
  public void sendToServer(String message) {
    serverHandler.send(message);
  }

  /**
   * Returns response from server
   * @return response from server
   */
  public String getLineFromServer() {
    return serverHandler.nextInputLine();
  }

  /**
   * Returns server mediator
   * @return server mediator
   */
  public ServerMediator getMediator() {
    return mediator;
  }

  /**
   * Returns client view
   * @return client view
   */
  public FtpClient getView() {
    return client;
  }

  /**
   * Returns send command controller
   * @param textSource text source for send command controller
   * @return send command controller
   */
  public ActionListener getSendCommandController(TextSource textSource) {
    return new SendCommandController(this, textSource);
  }

  /**
   * Refreshes local files.
   */
  public void refreshLocalFiles() {
    localFilesModel.refresh();
  }

  /**
   * Returns local files model
   * @return local files model
   */
  public FileSystemModel getLocalFilesModel() {
    return localFilesModel;
  }

}
