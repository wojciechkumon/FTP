package pl.edu.agh.kis.clientserverftp.launchers;

import pl.edu.agh.kis.clientserverftp.server.gui.AdminPanel;
import pl.edu.agh.kis.clientserverftp.server.gui.ServerPanelController;

/**
 * Admin Panel Launcher
 * @author Wojciech Kumoń
 */
public class ServerAdminPanelLauncher {

  public static void main(String[] args) {
    AdminPanel view = new AdminPanel();
    new ServerPanelController(view);
  }

}
