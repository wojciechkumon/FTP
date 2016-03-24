package pl.edu.agh.kis.clientserverftp.client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

/**
 * Controls logging user in.
 * @author Wojciech Kumoń
 *
 */
public class LoginController implements ActionListener {
  private ClientController controller;
  private LoginDataProvider dataProvider;

  /**
   * Constructs controller
   * @param controller client controller
   * @param dataProvider login data provider
   */
  public LoginController(ClientController controller, LoginDataProvider dataProvider) {
    this.controller = controller;
    this.dataProvider = dataProvider;
  }

  /**
   * Tries to connect.
   * @param event event
   */
  @Override
  public void actionPerformed(ActionEvent event) {
    LoginData loginData;
    try {
      loginData = getData();
    } catch (WrongLoginDataException e) {
      controller.showError(e.getMessage());
      return;
    }
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        controller.connect(loginData);
      }
    });
    
  }

  private LoginData getData() throws WrongLoginDataException {
    String host = dataProvider.getHost();
    String username = dataProvider.getUsername();
    String password = String.valueOf(dataProvider.getPassword());
    short port = getPort();
    
    if (host.isEmpty()) {
      throw new WrongLoginDataException("Host can't be empty!");
    }
    if (username.isEmpty()) {
      throw new WrongLoginDataException("Username can't be empty!");
    }
    
    return new LoginData(host, username, password, port);
  }

  private short getPort() throws WrongLoginDataException {
    try {
      return Short.parseShort(dataProvider.getPort());
    } catch (NumberFormatException e) {
      throw new WrongLoginDataException("Wrong port! Port must be number 0-65535. Default is 21.", e);
    }
  }

}
