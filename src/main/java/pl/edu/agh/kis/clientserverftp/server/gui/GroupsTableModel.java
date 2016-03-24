package pl.edu.agh.kis.clientserverftp.server.gui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import pl.edu.agh.kis.clientserverftp.server.database.Database;
import pl.edu.agh.kis.clientserverftp.server.database.Registry;

/**
 * Table Model for table with groups.
 * @author Wojciech Kumoń
 */
public class GroupsTableModel extends AbstractTableModel {
  private static final long serialVersionUID = -5694053210123267128L;
  private List<String> cache = new ArrayList<>();

  /**
   * Constructs table model.
   */
  public GroupsTableModel() {
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
    return "group name";
  }

  /**
   * Adds new group to database.
   * @param groupName group name
   */
  public void addGroup(String groupName) {
    try (Connection connection = Registry.createConnection()) {
      Database.getInstance().addGroup(connection, groupName);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    refreshCache();
  }

  private void refreshCache() {
    try (Connection connection = Registry.createConnection()) {
      cache = Database.getInstance().getAllGroups(connection);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    fireTableDataChanged();
  }

}
