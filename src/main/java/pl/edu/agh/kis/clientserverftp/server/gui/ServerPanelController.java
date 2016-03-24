package pl.edu.agh.kis.clientserverftp.server.gui;

import java.awt.event.ActionListener;

/**
 * Server panel controller
 * @author Wojciech Kumoń
 */
public class ServerPanelController {
  private AdminPanel view;

  /**
   * Contructs controller and sets view controlelr
   * @param view view which will get controller
   */
  public ServerPanelController(AdminPanel view) {
    this.view = view;
    view.setController(this);
  }

  /**
   * Returns listener which will switch table in view.
   * @param type table type
   * @return listener which will switch table in view
   */
  public ActionListener getSwitchTableListener(TableType type) {
    return l -> view.switchTable(type);
  }
  
}
