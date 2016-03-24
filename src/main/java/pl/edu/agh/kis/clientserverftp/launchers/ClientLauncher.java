package pl.edu.agh.kis.clientserverftp.launchers;

import pl.edu.agh.kis.clientserverftp.client.controller.ClientController;
import pl.edu.agh.kis.clientserverftp.client.controller.LoginController;
import pl.edu.agh.kis.clientserverftp.client.view.FtpClient;

/**
 * Ftp Client Launcher class
 * 
 * @author Wojciech Kumoń
 */
public class ClientLauncher {

  public static void main(String[] args) {
    FtpClient client = new FtpClient();
    ClientController controller = new ClientController(client);
    client.setLoginController(new LoginController(controller, client.getLoginDataProvider()));
  }

}
