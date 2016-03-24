package pl.edu.agh.kis.clientserverftp.server.gui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import pl.edu.agh.kis.clientserverftp.server.database.Database;
import pl.edu.agh.kis.clientserverftp.server.database.DbUser;
import pl.edu.agh.kis.clientserverftp.server.database.Registry;

/**
 * Table Model for table with users.
 * @author Wojciech Kumoń
 */
public class UsersTableModel extends AbstractTableModel {
  private static final long serialVersionUID = 5861358753919782816L;
  private List<String> cache = new ArrayList<>();

  /**
   * Constructs table model.
   */
  public UsersTableModel() {
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
    return 1;
  }

  /**
   * Return value at selected cell.
   * @param rowIndex row index
   * @param columnIndex column index
   * @return value at selected cell
   */
  @Override
  public String getValueAt(int rowIndex, int columnIndex) {
    return cache.get(rowIndex);
  }

  /**
   * Returns name of column.
   * @param column index of column
   * @return column name
   */
  @Override
  public String getColumnName(int column) {
    return "username";
  }

  /**
   * Adds user to database.
   * @param username username to add
   * @param password password for user
   */
  public void addUser(String username, char[] password) {
    DbUser dbUser = new DbUser(username, String.valueOf(password), DbUser.getRandomSalt());
    try (Connection connection = Registry.createConnection()) {
      Database.getInstance().addUser(connection, dbUser);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    refreshCache();
  }

  /**
   * Removes from database users which are in rows.
   * @param rows array with indexes of rows
   */
  public void removeRows(int[] rows) {
    List<String> usersToRemove = new ArrayList<>();
    for (int i = rows.length - 1; i >= 0; i--) {
      usersToRemove.add(cache.get(rows[i]));
    }
    try (Connection connection = Registry.createConnection()) {
      Database.getInstance().deleteUsers(connection, usersToRemove);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    refreshCache();
  }

  private void refreshCache() {
    try (Connection connection = Registry.createConnection()) {
      cache = Database.getInstance().getAllUsers(connection);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    fireTableDataChanged();
  }

}
