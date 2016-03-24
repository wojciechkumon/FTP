package pl.edu.agh.kis.clientserverftp.server.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DatabaseTest {
  private static final String TEST_DB_URL = "jdbc:h2:~/test_db";
  private static final String TEST_DB_USERNAME = "test";
  private static final String TEST_DB_PASSWIRD = "test";
  private static final Random RANDOM = new Random();
  private static Connection connection;

  private Database db = Database.getInstance();

  @BeforeClass
  public static void prepareDbAndConnection() throws SQLException {
    connection = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USERNAME, TEST_DB_PASSWIRD);
  }

  @AfterClass
  public static void clear() throws IOException, SQLException {
    try (Statement stmt = connection.createStatement()) {
      stmt.executeUpdate("DROP ALL OBJECTS DELETE FILES");
    }
    connection.close();
  }

  @Before
  public void clearDb() throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      stmt.executeUpdate("DROP ALL OBJECTS");
    }
  }

  @Test
  public void testCreatingTables() throws SQLException {
    db.createTables(connection);
    assertTrue(tableExist("USERS"));
    assertTrue(columnsExist("USERS", "ID", "USERNAME", "PASSWORD", "SALT"));

    assertTrue(tableExist("GROUPS"));
    assertTrue(columnsExist("GROUPS", "ID", "GROUP_NAME"));

    assertTrue(tableExist("USERGROUP"));
    assertTrue(columnsExist("USERGROUP", "USER_ID", "GROUP_ID"));

    assertTrue(tableExist("FILES"));
    assertTrue(columnsExist("FILES", "ID", "FILENAME", "OWNER_ID", "GROUP_ID", "USER_READ",
        "USER_WRITE", "GROUP_READ", "GROUP_WRITE"));
  }

  private boolean tableExist(String tableName) throws SQLException {
    try (ResultSet result = connection.getMetaData().getTables(null, null, tableName, null)) {
      return result.next();
    }
  }

  private boolean columnsExist(String tableName, String... columns) throws SQLException {
    boolean columnsExist = true;
    for (String col : columns) {
      try (ResultSet result = connection.getMetaData().getColumns(null, null, tableName, col)) {
        columnsExist = columnsExist && result.next();
      }
    }
    return columnsExist;
  }

  @Test
  public void testAddingUser() throws SQLException {
    String salt = getRandomSalt();
    String username = "nick";
    DbUser user = new DbUser(username, "pass", salt);
    db.createTables(connection);
    db.addUser(connection, user);
    try (Statement stmt = connection.createStatement()) {
      ResultSet result = stmt.executeQuery("SELECT * FROM users WHERE username='" + username + "'");
      if (result.next()) {
        assertEquals(salt, result.getString(4));
      } else {
        fail();
      }
    }
  }

  private String getRandomSalt() {
    byte[] saltBytes = new byte[16];
    RANDOM.nextBytes(saltBytes);
    return new String(saltBytes);
  }

  @Test
  public void testIsPasswordCorrect() {
    String salt = getRandomSalt();
    String username = "nick";
    String password = "pass";
    DbUser user = new DbUser(username, password, salt);
    db.createTables(connection);
    db.addUser(connection, user);
    assertTrue(Database.getInstance().isPasswordCorrect(connection, username, password));
    assertFalse(Database.getInstance().isPasswordCorrect(connection, username, "wrongPass"));
  }

  @Test
  public void testAddingGroup() throws SQLException {
    String groupName = "testGroup";
    db.createTables(connection);
    db.addGroup(connection, groupName);
    checkIfGroupExist(groupName);
  }

  private void checkIfGroupExist(String groupName) throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      ResultSet result =
          stmt.executeQuery("SELECT * FROM groups WHERE group_name='" + groupName + "'");
      assertTrue(result.next());
    }
  }

  @Test
  public void testAddingFileByDbFile() throws SQLException {
    db.createTables(connection);
    String path = "dir/file.txt";
    int userId = 17;
    int groupId = 43;
    DbFile file = new DbFile(path, userId, groupId);
    db.addFile(connection, file);

    checkFile(path, userId, groupId);
  }

  private void checkFile(String path, int userId, int groupId) throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      ResultSet result = stmt.executeQuery("SELECT * FROM files WHERE filename='" + path + "'");
      if (result.next()) {
        assertEquals(userId, result.getInt(3));
        assertEquals(groupId, result.getInt(4));
        assertEquals(1, result.getInt(5));
        assertEquals(1, result.getInt(6));
        assertEquals(1, result.getInt(7));
        assertEquals(0, result.getInt(8));
      } else {
        fail();
      }
    }
  }

  @Test
  public void testAddingFileByPathAndUsername() throws SQLException {
    String username = "name";
    initForAddingFileByPath(username);
    String path = "dir/file.txt";
    db.addFile(connection, path, username);
    checkFile(path, 1, 1);
  }

  private void initForAddingFileByPath(String username) {
    db.createTables(connection);
    String groupName = "group";
    db.addUser(connection, new DbUser(username, "pass", "salt"));
    db.addGroup(connection, groupName);
    db.addUserToGroup(connection, username, groupName);
  }

  @Test
  public void deleteFileTest() throws SQLException {
    db.createTables(connection);
    String path = "dir/file.txt";
    DbFile file = new DbFile(path, 1, 1);
    db.addFile(connection, file);
    db.deleteFile(connection, path);
    try (Statement stmt = connection.createStatement()) {
      ResultSet result = stmt.executeQuery("SELECT * FROM files WHERE filename='" + path + "'");
      assertFalse(result.next());
    }
  }

  @Test
  public void testAddingUserToGroup() throws SQLException {
    db.createTables(connection);
    String username = "testName";
    DbUser user = new DbUser(username, "pass", "salt");
    db.addUser(connection, user);

    String groupName = "testGroup";
    db.addGroup(connection, groupName);
    db.addUserToGroup(connection, username, groupName);

    try (Statement stmt = connection.createStatement()) {
      ResultSet result = stmt.executeQuery(
          "SELECT * FROM usergroup WHERE user_id=(SELECT id FROM users WHERE username='" + username
              + "')");
      if (result.next()) {
        assertEquals(1, result.getInt(2));
      } else {
        fail();
      }
    }
  }

  @Test
  public void testCreatingAdminGroup() throws SQLException {
    db.createTables(connection);
    db.createAdminGroup(connection);
    String groupName = "admin";
    checkIfGroupExist(groupName);
    db.createAdminGroup(connection);
    checkIfGroupExist(groupName);
  }

  @Test
  public void testIfCanReadFile() {
    db.createTables(connection);
    String username = "username";
    DbUser user = new DbUser(username, "password", "salt");
    db.addUser(connection, user);
    String groupName1 = "testGroup1";
    String groupName2 = "testGroup2";
    db.addGroup(connection, groupName1);
    db.addGroup(connection, groupName2);
    db.addUserToGroup(connection, username, groupName1);
    String fileName1 = "path/file1.txt";
    String fileName2 = "path/file2.txt";
    DbFile file1 = new DbFile(fileName1, 1, 1);
    DbFile file2 = new DbFile(fileName2, 2, 2);
    db.addFile(connection, file1);
    db.addFile(connection, file2);

    assertTrue(db.canRead(connection, 1, fileName1));
    assertFalse(db.canRead(connection, 1, fileName2));
  }

  @Test
  public void testIfCanWriteFile() {
    db.createTables(connection);
    String username = "username";
    DbUser user = new DbUser(username, "password", "salt");
    db.addUser(connection, user);
    String groupName1 = "testGroup1";
    String groupName2 = "testGroup2";
    db.addGroup(connection, groupName1);
    db.addGroup(connection, groupName2);
    db.addUserToGroup(connection, username, groupName1);
    String fileName1 = "path/file1.txt";
    String fileName2 = "path/file2.txt";
    DbFile file1 = new DbFile(fileName1, 1, 1);
    DbFile file2 = new DbFile(fileName2, 2, 2);
    db.addFile(connection, file1);
    db.addFile(connection, file2);

    assertTrue(db.canWrite(connection, 1, fileName1));
    assertFalse(db.canWrite(connection, 1, fileName2));
  }

  @Test
  public void getCorrectFileInfoTest() {
    db.createTables(connection);
    String path = "dir/file.txt";
    DbFile file = new DbFile(path, 1, 1);
    db.addFile(connection, file);
    String username = "name";
    db.addUser(connection, new DbUser(username, "pass", "salt"));
    String groupName = "group";
    db.addGroup(connection, groupName);
    db.addUserToGroup(connection, username, groupName);
    
    FileInfo info = db.getFileInfo(connection, path);
    assertEquals(path, info.getPath());
    assertEquals(username, info.getOwner());
    assertEquals(groupName, info.getGroup());
    assertTrue(info.isUserRead());
    assertTrue(info.isUserWrite());
    assertTrue(info.isGroupRead());
    assertFalse(info.isGroupWrite());
  }
  
  @Test
  public void tryGettingNotExisitingFileInfo() {
    db.createTables(connection);
    assertNull(db.getFileInfo(connection, "notExistingPath"));
  }

}
