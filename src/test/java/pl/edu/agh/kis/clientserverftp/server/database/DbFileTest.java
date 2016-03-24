package pl.edu.agh.kis.clientserverftp.server.database;

import static org.junit.Assert.*;

import org.junit.Test;

import pl.edu.agh.kis.clientserverftp.server.database.DbFile;

public class DbFileTest {

  @Test
  public void testObject() {
    String path = "path";
    int userId = 17;
    int groupId = 37;
    DbFile file = new DbFile(path, userId, groupId);
    assertEquals(path, file.getPath());
    assertEquals(userId, file.getUserId());
    assertEquals(groupId, file.getGroupId());
  }

}
