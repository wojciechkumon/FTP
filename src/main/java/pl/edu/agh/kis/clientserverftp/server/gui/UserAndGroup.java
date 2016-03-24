package pl.edu.agh.kis.clientserverftp.server.gui;

/**
 * Class which allows representing user and hid group as one object. 
 * @author Wojciech Kumoń
 */
public final class UserAndGroup {
  private final String groupName;
  private final String username;

  /**
   * Contructs object
   * @param username username
   * @param groupName group name
   */
  public UserAndGroup(String username, String groupName) {
    this.username = username;
    this.groupName = groupName;
  }

  /**
   * Returns username.
   * @return username
   */
  public String getUsername() {
    return username;
  }
  
  /**
   * Returns group name.
   * @return group name
   */
  public String getGroupName() {
    return groupName;
  }
  
}
