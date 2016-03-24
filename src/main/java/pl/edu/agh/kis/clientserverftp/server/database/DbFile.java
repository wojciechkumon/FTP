package pl.edu.agh.kis.clientserverftp.server.database;

/**
 * Class which represents database file with minimum informations.
 * @author Wojciech Kumoń
 */
public final class DbFile {
  private final String path;
  private final int userId;
  private final int groupId;

  /**
   * Construct DbFile.
   * @param path relative path to file
   * @param userId owner id
   * @param groupId owner group id
   */
  public DbFile(String path, int userId, int groupId) {
    this.path = path;
    this.userId = userId;
    this.groupId = groupId;
  }

  /**
   * Returns relative path to file.
   * @return relative path to file
   */
  public String getPath() {
    return path;
  }

  /**
   * Return owner id. 
   * @return owner id
   */
  public int getUserId() {
    return userId;
  }

  /**
   * Returns owner group id.
   * @return owner group id
   */
  public int getGroupId() {
    return groupId;
  }

}
