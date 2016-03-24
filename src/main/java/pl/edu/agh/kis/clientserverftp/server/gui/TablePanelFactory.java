package pl.edu.agh.kis.clientserverftp.server.gui;

/**
 * Table panels factory.
 * @author Wojciech Kumoń
 */
public class TablePanelFactory {
  private TablePanelFactory() {}

  /**
   * Method producing table panels
   * @param type type of panel to create
   * @return created TablePanel
   */
  public static TablePanel create(TableType type) {
    switch (type) {
      case USERS:
        return new UsersPanel();
      case GROUPS:
        return new GroupsPanel();
      case USER_GROUPS_MANAGEMENT:
        return new GroupsManagementPanel();
      default:
        throw new RuntimeException("Wrong TableType: " + type);
    }
  }

}
