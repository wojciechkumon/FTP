package pl.edu.agh.kis.clientserverftp.server.gui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import pl.edu.agh.kis.clientserverftp.server.database.Database;
import pl.edu.agh.kis.clientserverftp.server.database.Registry;

/**
 * Table Model for table with users and their groups.
 * @author Wojciech Kumoń
 */
public class GroupsManagementTableModel extends AbstractTableModel {
  private static final long serialVersionUID = 8717604148886557890L;
  private List<UserAndGroup> cache = new ArrayList<>();

  /**
   * Constructs table model.
   */
  public GroupsManagementTableModel() {
    refreshCache();
  }

  /**
   * Returns row count.
   * @return row count
   */
  @Override
  public int getRowCount() {
    return cache.size();
  }

  /**
   * Returns column count.
   * @return column count
   */
  @Override
  public int getColumnCount() {
    return 2;
  }

  /**
   * Return value at selected cell.
   * @param rowIndex row index
   * @param columnIndex column index
   * @return value at selected cell
   */
  @Override
  public String getValueAt(int rowIndex, int columnIndex) {
    if (columnIndex == 0) {
      return cache.get(rowIndex).getUsername();
    }
    return cache.get(rowIndex).getGroupName();
  }

  /**
   * Returns name of column.
   * @param column index of column
   * @return column name
   */
  @Override
  public String getColumnName(int column) {
    if (column == 0) {
      return "username";
    }
    return "group name";
  }

  /**
   * Returns list of all users.
   * @return list of all users
   */
  public List<String> getAllUsers() {
    try (Connection connection = Registry.createConnection()) {
      return Database.getInstance().getAllUsers(connection);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Collections.emptyList();
  }

  /**
   * Returns list of all groups.
   * @return list of all groups
   */
  public List<String> getAllGroups() {
    try (Connection connection = Registry.createConnection()) {
      return Database.getInstance().getAllGroups(connection);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Collections.emptyList();
  }

  /**
   * Assigns user to groups. Database will be changed.
   * @param username user to assign
   * @param groupName group where user will be added
   */
  public void addUserToGroup(String username, String groupName) {
    Database db = Database.getInstance();
    try (Connection connection = Registry.createConnection()) {
      if (db.userExists(connection, username) && db.groupExists(connection, groupName)) {
        db.addUserToGroup(connection, username, groupName);
        refreshCache();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Removes users from groups if rows where selected. Database will be changed.
   * @param rows array with selected rows indexes
   */
  public void removeRows(int[] rows) {
    List<UserAndGroup> usersToDeleteFromGroups = new ArrayList<>();
    for (int i = rows.length - 1; i >= 0; i--) {
      usersToDeleteFromGroups.add(cache.get(rows[i]));
    }
    try (Connection connection = Registry.createConnection()) {
      Database.getInstance().deleteUsersFromGroups(connection, usersToDeleteFromGroups);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    refreshCache();
  }

  private void refreshCache() {
    try (Connection connection = Registry.createConnection()) {
      cache = Database.getInstance().getAllUsersAndGroups(connection);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    fireTableDataChanged();
  }

}
