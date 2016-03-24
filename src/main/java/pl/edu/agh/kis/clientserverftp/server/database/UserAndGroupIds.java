package pl.edu.agh.kis.clientserverftp.server.database;

class UserAndGroupIds {
  private final int userId;
  private final int groupId;

  public UserAndGroupIds(int user, int group) {
    userId = user;
    groupId = group;
  }

  public int getUserId() {
    return userId;
  }

  public int getGroupId() {
    return groupId;
  }

}
