package pl.edu.agh.kis.clientserverftp.server.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Panel which allows switching tables.
 * @author Wojciech Kumoń
 */
public class SwitchViewPanel extends JPanel {
  private static final long serialVersionUID = 3735317541501293206L;
  private JButton usersBtn = new JButton("Users");
  private JButton groupsBtn = new JButton("Groups");
  private JButton userGroupsBtn = new JButton("User groups management");

  /**
   * Contructs panel.
   */
  public SwitchViewPanel() {
    setLayout(new FlowLayout());
    Dimension btnSize = new Dimension(100, 60);
    usersBtn.setPreferredSize(btnSize);
    groupsBtn.setPreferredSize(btnSize);
    userGroupsBtn.setPreferredSize(new Dimension(220, 60));

    add(usersBtn);
    add(groupsBtn);
    add(userGroupsBtn);
  }

  /**
   * Sets controller for buttons
   * @param serverPanelController controller which will return listeners
   */
  public void setController(ServerPanelController serverPanelController) {
    usersBtn.addActionListener(serverPanelController.getSwitchTableListener(TableType.USERS));
    groupsBtn.addActionListener(serverPanelController.getSwitchTableListener(TableType.GROUPS));
    userGroupsBtn.addActionListener(
        serverPanelController.getSwitchTableListener(TableType.USER_GROUPS_MANAGEMENT));
  }

}
