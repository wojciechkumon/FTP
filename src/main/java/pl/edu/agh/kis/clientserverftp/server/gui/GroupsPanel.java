package pl.edu.agh.kis.clientserverftp.server.gui;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Panel with group table.
 * @author Wojciech Kumoń
 */
public class GroupsPanel extends TablePanel {
  private static final long serialVersionUID = 3111527403003642971L;

  private JButton addGroupBtn = new JButton("Add group");
  private GroupsTableModel tableModel;

  /**
   * Constructs panel.
   */
  public GroupsPanel() {
    init();
  }

  private void init() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    JPanel btnPanel = new JPanel();
    btnPanel.setLayout(new FlowLayout());
    
    tableModel = new GroupsTableModel();

    JTable table = new JTable(tableModel);
    table.getTableHeader().setReorderingAllowed(false);

    addActionListener();

    btnPanel.add(addGroupBtn);
    add(btnPanel);
    add(table);
    add(new JScrollPane(table));
  }

  private void addActionListener() {
    addGroupBtn.addActionListener(l -> {
      String groupName = null;
      groupName = JOptionPane.showInputDialog(GroupsPanel.this, "Enter group name");
      if (groupName != null && !groupName.isEmpty()) {
        tableModel.addGroup(groupName);
      }
    });
  }
}
