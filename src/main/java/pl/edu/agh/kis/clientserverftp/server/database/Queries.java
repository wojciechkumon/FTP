package pl.edu.agh.kis.clientserverftp.server.database;

import java.util.List;

/**
 * Database queries
 * @author Wojciech Kumoń
 *
 */
public class Queries {
  static final String INSERT_USER = "INSERT INTO `users`(`username`, `password`, `salt`) "
      + "VALUES (?,HASH('SHA256', STRINGTOUTF8((CONCAT(HASH('SHA256', STRINGTOUTF8(?), 1000),?))), 1000),?)";

  static final String INSERT_FILE = "INSERT INTO `files`"
      + "(`filename`, `owner_id`, `group_id`, `user_read`, `user_write`, `group_read`, `group_write`) "
      + "VALUES (?,?,?,TRUE,TRUE,TRUE,FALSE)";

  static final String INSERT_GROUP_FOR_USER = "INSERT INTO `usergroup`(`user_id`,`group_id`) "
      + "VALUES ((SELECT `id` FROM `users` WHERE `username`=?),"
      + "(SELECT `id` FROM `groups` WHERE `group_name`=?))";

  static final String INSERT_GROUP = "INSERT INTO `groups` (`group_name`) VALUES(?);";

  static final String DELETE_FILE = "DELETE FROM files WHERE filename=?";

  static final String CHECK_IF_GROUP_EXIST = "SELECT id FROM groups WHERE group_name=?";

  static final String CHECK_PASSWORD =
      "SELECT `password`=HASH('SHA256', STRINGTOUTF8((CONCAT(HASH('SHA256', STRINGTOUTF8(?), 1000),`salt`))), 1000) "
          + "FROM `users` WHERE `username`=?";

  static final String CAN_READ =
      "SELECT `group_read` OR (`user_read` AND `owner_id`=?) FROM `files` AS `f` "
          + "WHERE `f`.`filename`=? AND `f`.`group_id` IN "
          + "(SELECT `group_id` FROM `usergroup` WHERE `user_id`=?)";

  static final String CAN_WRITE =
      "SELECT `group_write` OR (`user_write` AND `owner_id`=?) FROM `files` AS `f` "
          + "WHERE `f`.`filename`=? AND `f`.`group_id` IN "
          + "(SELECT `group_id` FROM `usergroup` WHERE `user_id`=?)";

  static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS users ("
      + "id INT(1) NOT NULL AUTO_INCREMENT," 
      + "username VARCHAR(10) NOT NULL,"
      + "password VARCHAR(64) NOT NULL," 
      + "salt VARCHAR(20) NOT NULL," 
      + "PRIMARY KEY  (id),"
      + "UNIQUE KEY username (username)" 
      + ") AUTO_INCREMENT=3";

  static final String CREATE_GROUPS_TABLE = "CREATE TABLE IF NOT EXISTS `groups` ("
      + "`id` INT(1) NOT NULL AUTO_INCREMENT," 
      + "`group_name` VARCHAR(10) NOT NULL,"
      + "PRIMARY KEY  (`id`)," 
      + "UNIQUE KEY `group_name` (`group_name`)" 
      + ") AUTO_INCREMENT=2";

  static final String CREATE_FILES_TABLE =
      "CREATE TABLE IF NOT EXISTS `files` (" 
          + "`id` INT(11) NOT NULL AUTO_INCREMENT,"
          + "`filename` VARCHAR(50) NOT NULL," 
          + "`owner_id` INT(11) NOT NULL,"
          + "`group_id` INT(11) NOT NULL," 
          + "`user_read` tinyint(1) NOT NULL DEFAULT '1',"
          + "`user_write` tinyint(1) NOT NULL DEFAULT '1',"
          + "`group_read` tinyint(1) NOT NULL DEFAULT '0',"
          + "`group_write` tinyint(1) NOT NULL DEFAULT '0'," 
          + "PRIMARY KEY  (`id`),"
          + "UNIQUE KEY `filename` (`filename`)" 
          + ") AUTO_INCREMENT=2";

  static final String CREATE_USERGROUP_TABLE = "CREATE TABLE IF NOT EXISTS `usergroup` ("
      + "`user_id` INT(11) NOT NULL," 
      + "`group_id` INT(11) NOT NULL" + ")";

  static final String GET_FILE_BY_PATH = "SELECT " 
          + "files.filename, users.username, groups.group_name, "
          + "files.user_read, files.user_write, files.group_read, files.group_write "
          + "FROM files, users, groups "
          + "WHERE files.owner_id=users.id AND files.group_id=groups.id " + "AND filename=?";
  
  static final String GET_IDS_BY_USERNAME =
      "SELECT users.id,group_id FROM users, usergroup, groups WHERE "
      + "users.id=usergroup.user_id AND groups.id=usergroup.group_id AND username=?";

  static final String GET_ALL_USERS = "SELECT username FROM users";

  static final String GET_ALL_GROUPS = "SELECT group_name FROM groups";

  static final String SELECT_USER = "SELECT id, username, password, salt FROM users where username=?";

  static final String SELECT_GROUP = "SELECT * FROM groups where group_name=?";
  
  public static final String SELECT_FILE = "SELECT * FROM files where filename=?";
  
  static final String GET_ALL_USERS_GROUPS = "SELECT users.username, groups.group_name FROM "
      + "users, groups, usergroup WHERE usergroup.user_id=users.id AND usergroup.group_id=groups.id";

  static final String SELECT_USER_AND_GROUP_ID = "SELECT usergroup.user_id, usergroup.group_id FROM "
      + "users, groups, usergroup WHERE usergroup.user_id=users.id AND usergroup.group_id=groups.id "
      + "AND users.username=? AND groups.group_name=?";

  public static final String CHMOD = "UPDATE files "
      + "SET USER_READ=?, USER_WRITE=?, GROUP_READ=?, GROUP_WRITE=? "
      + "WHERE FILENAME=?";

  static String getRemoveUsersQuery(List<String> usersToRemove) {
    StringBuilder query = new StringBuilder("DELETE FROM users WHERE");
    if (usersToRemove.isEmpty()) {
      query.append(" 1=0");
      return query.toString();
    }
    for (String username : usersToRemove) {
      query.append(" username='");
      query.append(username);
      query.append("' OR");
    }
    query.delete(query.length() - 2, query.length());
    return query.toString();
  }
  
  static String getRemoveUsersFromGroupsQuery(List<UserAndGroupIds> ids) {
    StringBuilder query = new StringBuilder("DELETE FROM usergroup WHERE");
    if (ids.isEmpty()) {
      query.append(" 1=0");
      return query.toString();
    }
    for (UserAndGroupIds id : ids) {
      query.append(" (user_id=");
      query.append(id.getUserId());
      query.append(" AND group_id=");
      query.append(id.getGroupId());
      query.append(") OR");
    }
    query.delete(query.length() - 2, query.length());
    return query.toString();
  }
  
  private Queries() {}
  
}
