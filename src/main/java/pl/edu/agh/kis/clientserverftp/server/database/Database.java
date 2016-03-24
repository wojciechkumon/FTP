package pl.edu.agh.kis.clientserverftp.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import pl.edu.agh.kis.clientserverftp.server.database.FileInfo.FileInfoBuilder;
import pl.edu.agh.kis.clientserverftp.server.gui.UserAndGroup;

/**
 * Singleton class which allows modifying database.
 * 
 * @author Wojciech Kumoń
 */
public class Database {
  private static Database instance = new Database();

  private Database() {}

  /**
   * Returns database intance (singleton).
   * 
   * @return database instance
   */
  public static Database getInstance() {
    return instance;
  }

  /**
   * Checks if user exists
   * 
   * @param connection connection with database
   * @param username user to check
   * @return true if user exists, false otherwise
   */
  public boolean userExists(Connection connection, String username) {
    try (PreparedStatement stmt = connection.prepareStatement(Queries.SELECT_USER)) {
      stmt.setString(1, username);
      ResultSet result = stmt.executeQuery();
      return result.next();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Returns user id or -1 if not exists
   * 
   * @param connection connection with database
   * @param username user to get his id
   * @return id or -1 if user not exists
   */
  public int getUserId(Connection connection, String username) {
    try (PreparedStatement stmt = connection.prepareStatement(Queries.SELECT_USER)) {
      stmt.setString(1, username);
      ResultSet result = stmt.executeQuery();
      if (result.next()) {
        return result.getInt(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return -1;
  }

  /**
   * Adds new user to database.
   * 
   * @param connection connection with database
   * @param user user to add
   */
  public void addUser(Connection connection, DbUser user) {
    if (userExists(connection, user.getUsername())) {
      return;
    }
    try (PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_USER)) {
      stmt.setString(1, user.getUsername());
      stmt.setString(2, user.getPassword());
      stmt.setString(3, user.getSalt());
      stmt.setString(4, user.getSalt());
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns list with all users.
   * 
   * @param connection connection with database
   * @return list with all usernames from database
   */
  public List<String> getAllUsers(Connection connection) {
    List<String> users = new ArrayList<>();
    try (PreparedStatement stmt = connection.prepareStatement(Queries.GET_ALL_USERS)) {
      ResultSet result = stmt.executeQuery();
      while (result.next()) {
        users.add(result.getString(1));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return users;
  }

  /**
   * Removes from database all users whose usernames are on the list.
   * 
   * @param connection connection with database
   * @param usersToRemove list of users to remove
   */
  public void deleteUsers(Connection connection, List<String> usersToRemove) {
    try (PreparedStatement stmt =
        connection.prepareStatement(Queries.getRemoveUsersQuery(usersToRemove))) {
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  /**
   * Adds informations about new file to database.
   * 
   * @param connection connection with database
   * @param file new file
   */
  public void addFile(Connection connection, DbFile file) {
    try (PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_FILE)) {
      stmt.setString(1, file.getPath());
      stmt.setInt(2, file.getUserId());
      stmt.setInt(3, file.getGroupId());
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Checks if file exists
   * 
   * @param connection connection with database
   * @param filePath file absolute path
   * @return true if file exists, false otherwise
   */
  public boolean fileExists(Connection connection, String filePath) {
    try (PreparedStatement stmt = connection.prepareStatement(Queries.SELECT_FILE)) {
      stmt.setString(1, filePath);
      ResultSet result = stmt.executeQuery();
      return result.next();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Adds informations about new file to database.
   * 
   * @param connection connection with database
   * @param filePath relative path to file
   * @param username file owner username
   */
  public void addFile(Connection connection, String filePath, String username) {
    try (PreparedStatement stmt = connection.prepareStatement(Queries.GET_IDS_BY_USERNAME)) {
      stmt.setString(1, username);
      ResultSet result = stmt.executeQuery();
      result.next();
      DbFile file = new DbFile(filePath, result.getInt(1), result.getInt(2));
      addFile(connection, file);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Deletes informations about file from database.
   * 
   * @param connection connection with database
   * @param filePath relative path to file
   */
  public void deleteFile(Connection connection, String filePath) {
    try (PreparedStatement stmt = connection.prepareStatement(Queries.DELETE_FILE)) {
      stmt.setString(1, filePath);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Adds user to group.
   * 
   * @param connection connection with database
   * @param username user which will be in new group
   * @param groupName new group for user
   */
  public void addUserToGroup(Connection connection, String username, String groupName) {
    if (isUserInGroup(connection, username, groupName)) {
      return;
    }
    try (PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_GROUP_FOR_USER)) {
      stmt.setString(1, username);
      stmt.setString(2, groupName);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Removes users from groups.
   * 
   * @param connection connection with database
   * @param usersToRemove list of users to remove from from groups
   */
  public void deleteUsersFromGroups(Connection connection, List<UserAndGroup> usersToRemove) {
    List<UserAndGroupIds> ids = getUserAndGroupIds(connection, usersToRemove);
    try (PreparedStatement stmt =
        connection.prepareStatement(Queries.getRemoveUsersFromGroupsQuery(ids))) {
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private List<UserAndGroupIds> getUserAndGroupIds(Connection connection,
      List<UserAndGroup> usersToRemove) {
    List<UserAndGroupIds> ids = new ArrayList<>();
    for (UserAndGroup userGroup : usersToRemove) {
      addIdsToList(connection, ids, userGroup);
    }
    return ids;
  }

  private void addIdsToList(Connection connection, List<UserAndGroupIds> ids,
      UserAndGroup userGroup) {
    if (userExists(connection, userGroup.getUsername())
        && groupExists(connection, userGroup.getGroupName())) {
      UserAndGroupIds oneUserGroupId = getUserAndGroupId(connection, userGroup);
      if (oneUserGroupId != null) {
        ids.add(oneUserGroupId);
      }
    }
  }

  private UserAndGroupIds getUserAndGroupId(Connection connection, UserAndGroup userGroup) {
    try (PreparedStatement stmt = connection.prepareStatement(Queries.SELECT_USER_AND_GROUP_ID)) {
      stmt.setString(1, userGroup.getUsername());
      stmt.setString(2, userGroup.getGroupName());
      ResultSet result = stmt.executeQuery();
      if (result.next()) {
        return new UserAndGroupIds(result.getInt(1), result.getInt(2));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Checks if group exists
   * 
   * @param connection connection with database
   * @param groupName group to check
   * @return true if group exists, false otherwise
   */
  public boolean groupExists(Connection connection, String groupName) {
    try (PreparedStatement stmt = connection.prepareStatement(Queries.SELECT_GROUP)) {
      stmt.setString(1, groupName);
      ResultSet result = stmt.executeQuery();
      return result.next();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Returns list with all users.
   * 
   * @param connection connection with database
   * @return list with all group names from database
   */
  public List<String> getAllGroups(Connection connection) {
    List<String> groups = new ArrayList<>();
    try (PreparedStatement stmt = connection.prepareStatement(Queries.GET_ALL_GROUPS)) {
      ResultSet result = stmt.executeQuery();
      while (result.next()) {
        groups.add(result.getString(1));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return groups;
  }

  /**
   * Adds new group to database.
   * 
   * @param connection connection with database
   * @param groupName name of new group
   */
  public void addGroup(Connection connection, String groupName) {
    if (groupExists(connection, groupName)) {
      return;
    }
    try (PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_GROUP)) {
      stmt.setString(1, groupName);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns list with which contains rows with user and his group.
   * 
   * @param connection connection with database
   * @return list with which contains rows with user and his group
   */
  public List<UserAndGroup> getAllUsersAndGroups(Connection connection) {
    List<UserAndGroup> groups = new ArrayList<>();
    try (PreparedStatement stmt = connection.prepareStatement(Queries.GET_ALL_USERS_GROUPS)) {
      ResultSet result = stmt.executeQuery();
      while (result.next()) {
        groups.add(new UserAndGroup(result.getString(1), result.getString(2)));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return groups;
  }

  public boolean isUserInGroup(Connection connection, String username, String groupName) {
    return getUserAndGroupId(connection, new UserAndGroup(username, groupName)) != null;
  }

  /**
   * Creates admin group if not exists.
   * 
   * @param connection connection with database
   */
  public void createAdminGroup(Connection connection) {
    try (PreparedStatement stmt = connection.prepareStatement(Queries.CHECK_IF_GROUP_EXIST)) {
      String groupName = "admin";
      stmt.setString(1, groupName);
      if (!stmt.executeQuery().next()) {
        addGroup(connection, groupName);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns info about specyfied file.
   * 
   * @param connection connection with database
   * @param path relative path to file
   * @return all info about this file which was stored in database
   */
  public FileInfo getFileInfo(Connection connection, String path) {
    try (PreparedStatement stmt = connection.prepareStatement(Queries.GET_FILE_BY_PATH)) {
      stmt.setString(1, path);
      ResultSet result = stmt.executeQuery();
      if (result.next()) {
        return createInfoFile(connection, result);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  private FileInfo createInfoFile(Connection connection, ResultSet result) throws SQLException {
    FileInfoBuilder builder = new FileInfoBuilder().path(result.getString(1));
    builder.owner(result.getString(2));
    builder.group(result.getString(3)).userPermissions(result.getBoolean(4), result.getBoolean(5));
    builder.groupPermissions(result.getBoolean(6), result.getBoolean(7));
    return builder.build();
  }

  /**
   * Checks if password is correct for specyfied user.
   * 
   * @param connection connection with database
   * @param username username whose password will be checked
   * @param password password to check
   * @return true if password is correct for specyfied user, false otherwise
   */
  public boolean isPasswordCorrect(Connection connection, String username, String password) {
    try (PreparedStatement stmt = connection.prepareStatement(Queries.CHECK_PASSWORD)) {
      stmt.setString(1, password);
      stmt.setString(2, username);
      ResultSet result = stmt.executeQuery();
      if (result.next()) {
        return convertStringToBoolean(result.getString(1));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  private boolean convertStringToBoolean(String string) {
    if (string.equals("TRUE")) {
      return true;
    }
    return false;
  }

  public void changePermissions(Connection connection, FileInfo info) {
    try (PreparedStatement stmt = connection.prepareStatement(Queries.CHMOD)) {
      stmt.setBoolean(1, info.isUserRead());
      stmt.setBoolean(2, info.isUserWrite());
      stmt.setBoolean(3, info.isGroupRead());
      stmt.setBoolean(4, info.isGroupWrite());
      stmt.setString(5, info.getPath());
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Checks if specyfied user can read a file.
   * 
   * @param connection connection with database
   * @param userId user id
   * @param fileName relative path to file which will be checked
   * @return true if user can read specyfied file, false otherwise
   */
  public boolean canRead(Connection connection, int userId, String fileName) {
    return getBooleanFromReadWrite(connection, userId, fileName, Queries.CAN_READ);
  }

  /**
   * Checks if specyfied user have write permissions to file.
   * 
   * @param connection connection with database
   * @param userId user id
   * @param fileName relative path to file which will be checked
   * @return true if user have write permissions to specyfied file, false otherwise
   */
  public boolean canWrite(Connection connection, int userId, String fileName) {
    return getBooleanFromReadWrite(connection, userId, fileName, Queries.CAN_WRITE);
  }

  private boolean getBooleanFromReadWrite(Connection connection, int userId, String fileName,
      String query) {
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      stmt.setInt(1, userId);
      stmt.setString(2, fileName);
      stmt.setInt(3, userId);
      ResultSet result = stmt.executeQuery();
      if (result.next()) {
        return result.getBoolean(1);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Creates all needed tables.
   * 
   * @param connection connection with database
   */
  public void createTables(Connection connection) {
    try (Statement stmt = connection.createStatement()) {
      stmt.addBatch(Queries.CREATE_USERS_TABLE);
      stmt.addBatch(Queries.CREATE_GROUPS_TABLE);
      stmt.addBatch(Queries.CREATE_FILES_TABLE);
      stmt.addBatch(Queries.CREATE_USERGROUP_TABLE);
      stmt.executeBatch();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
