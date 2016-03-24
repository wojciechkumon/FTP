package pl.edu.agh.kis.clientserverftp.server.database;

/**
 * Class representing full informations about file in database.
 * FileInfo objects can be created only with FileInfoBuilder class.
 * @author Wojciech Kumoń
 */
public class FileInfo {
  private final String path;
  private final String owner;
  private final String group;
  private final boolean userRead;
  private final boolean userWrite;
  private final boolean groupRead;
  private final boolean groupWrite;
  
  private FileInfo(FileInfoBuilder builder) {
    path = builder.path;
    owner = builder.owner;
    group = builder.group;
    userRead = builder.userRead;
    userWrite = builder.userWrite;
    groupRead = builder.groupRead;
    groupWrite = builder.groupWrite;
  }

  /**
   * Returns relative path to file.
   * @return relative path to file
   */
  public String getPath() {
    return path;
  }

  /**
   * Return owner's username.
   * @return owner's username
   */
  public String getOwner() {
    return owner;
  }

  /**
   * Return owner's group name.
   * @return owner's group name
   */
  public String getGroup() {
    return group;
  }

  /**
   * Checks if user has read permissions
   * @return true if user has read permissions, false otherwise
   */
  public boolean isUserRead() {
    return userRead;
  }

  /**
   * Checks if user has write permissions
   * @return true if user has write permissions, false otherwise
   */
  public boolean isUserWrite() {
    return userWrite;
  }

  /**
   * Checks if group has read permissions
   * @return true if group has read permissions, false otherwise
   */
  public boolean isGroupRead() {
    return groupRead;
  }

  /**
   * Checks if group has write permissions
   * @return true if group has write permissions, false otherwise
   */
  public boolean isGroupWrite() {
    return groupWrite;
  }


  /**
   * FileInfo builder
   * @author Wojciech Kumoń
   *
   */
  public static class FileInfoBuilder {
    private String path;
    private String owner;
    private String group;
    private boolean userRead;
    private boolean userWrite;
    private boolean groupRead;
    private boolean groupWrite;

    /**
     * Sets path
     * @param path path to set
     * @return this
     */
    public FileInfoBuilder path(String path) {
      this.path = path;
      return this;
    }

    /**
     * Sets owner
     * @param owner owner to set
     * @return this
     */
    public FileInfoBuilder owner(String owner) {
      this.owner = owner;
      return this;
    }

    /**
     * Sets group
     * @param group group to set
     * @return this
     */
    public FileInfoBuilder group(String group) {
      this.group = group;
      return this;
    }

    /**
     * Sets user permissions
     * @param userRead user read permissions
     * @param userWrite user write permissions
     * @return this
     */
    public FileInfoBuilder userPermissions(boolean userRead, boolean userWrite) {
      this.userRead = userRead;
      this.userWrite = userWrite;
      return this;
    }

    /**
     * Sets group permissions
     * @param groupRead group read permissions
     * @param groupWrite group write permissions
     * @return this
     */
    public FileInfoBuilder groupPermissions(boolean groupRead, boolean groupWrite) {
      this.groupRead = groupRead;
      this.groupWrite = groupWrite;
      return this;
    }
    
    /**
     * Build FileInfo
     * @return FileInfo object
     */
    public FileInfo build() {
      return new FileInfo(this);
    }

  }

}
