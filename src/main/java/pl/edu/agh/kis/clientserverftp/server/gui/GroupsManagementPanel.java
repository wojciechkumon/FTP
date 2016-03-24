package pl.edu.agh.kis.clientserverftp.server.gui;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Panel with table to hold users and their groups.
 * @author Wojciech Kumoń
 */
public class GroupsManagementPanel extends TablePanel {
  private static final long serialVersionUID = 6433790871155242612L;

  private JButton addUsersToGroupBtn = new JButton("Add user to group");
  private JButton removeUsersFromGroupBtn = new JButton("Remove selected users from group");

  private GroupsManagementTableModel tableModel;

  /**
   * Contructs panel.
   */
  public GroupsManagementPanel() {
    init();
  }

  private void init() {
    JPanel btnPanel = new JPanel();
    btnPanel.setLayout(new FlowLayout());
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    tableModel = new GroupsManagementTableModel();

    JTable table = new JTable(tableModel);
    table.getTableHeader().setReorderingAllowed(false);

    addActionListeners(table);

    btnPanel.add(addUsersToGroupBtn);
    btnPanel.add(removeUsersFromGroupBtn);
    add(btnPanel);
    add(table);
    add(new JScrollPane(table));
  }

  private void addActionListeners(JTable table) {
    addUsersToGroupBtn.addActionListener(l -> {
      String[] users = tableModel.getAllUsers().toArray(new String[0]);
      String[] groups = tableModel.getAllGroups().toArray(new String[0]);

      JComboBox<String> usersComboBox = new JComboBox<>(users);
      JComboBox<String> groupsComboBox = new JComboBox<>(groups);

      JPanel myPanel = new JPanel();
      myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
      myPanel.add(new JLabel("Username:"));
      myPanel.add(usersComboBox);
      myPanel.add(new JLabel("Group name:"));
      myPanel.add(groupsComboBox);
      int result = JOptionPane.showConfirmDialog(GroupsManagementPanel.this, myPanel,
          "Adding new user:", JOptionPane.OK_CANCEL_OPTION);
      if (result != JOptionPane.OK_OPTION) {
        return;
      }
      tableModel.addUserToGroup((String) usersComboBox.getSelectedItem(),
          (String) groupsComboBox.getSelectedItem());
    });

    removeUsersFromGroupBtn.addActionListener(l -> {
      removeSelectedRows(table);
    });
  }

  private void removeSelectedRows(JTable table) {
    int[] rows = table.getSelectedRows();
    tableModel.removeRows(rows);
  }
}
