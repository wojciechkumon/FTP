package pl.edu.agh.kis.clientserverftp.server.gui;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * Panel with users table.
 * @author Wojciech Kumoń
 */
public class UsersPanel extends TablePanel {
  private static final long serialVersionUID = 5674474977432261745L;

  private JButton addUserBtn = new JButton("Add user");
  private JButton removeSelectedUsersBtn = new JButton("Remove selected");

  private UsersTableModel tableModel;

  /**
   * Contructs panel.
   */
  public UsersPanel() {
    init();
  }

  private void removeSelectedRows(JTable table) {
    int[] rows = table.getSelectedRows();
    tableModel.removeRows(rows);
  }

  private void init() {
    JPanel btnPanel = new JPanel();
    btnPanel.setLayout(new FlowLayout());
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    
    tableModel = new UsersTableModel();

    JTable table = new JTable(tableModel);
    table.getTableHeader().setReorderingAllowed(false);

    addActionListeners(table);

    btnPanel.add(addUserBtn);
    btnPanel.add(removeSelectedUsersBtn);
    add(btnPanel);
    add(table);
    add(new JScrollPane(table));
  }

  private void addActionListeners(JTable table) {
    addUserBtn.addActionListener(l -> {
      JTextField usernameField = new JTextField(8);
      JPasswordField passwordField = new JPasswordField(8);
      JPanel myPanel = new JPanel();
      myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
      myPanel.add(new JLabel("Username:"));
      myPanel.add(usernameField);
      myPanel.add(new JLabel("Password:"));
      myPanel.add(passwordField);
      int result = JOptionPane.showConfirmDialog(UsersPanel.this, myPanel,
          "Adding new user:", JOptionPane.OK_CANCEL_OPTION);
      if (result != JOptionPane.OK_OPTION) {
        return;
      }
      tableModel.addUser(usernameField.getText(), passwordField.getPassword());
    });

    removeSelectedUsersBtn.addActionListener(l -> {
      removeSelectedRows(table);
    });
  }

}
