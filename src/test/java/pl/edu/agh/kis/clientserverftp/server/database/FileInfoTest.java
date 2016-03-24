package pl.edu.agh.kis.clientserverftp.server.database;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pl.edu.agh.kis.clientserverftp.server.database.FileInfo.FileInfoBuilder;

public class FileInfoTest {

  @Test
  public void testFileInfo() {
    String path = "path";
    String owner = "owner";
    String group = "group";
    boolean userRead = true;
    boolean userWrite = false;
    boolean groupRead = false;
    boolean groupWrite = true;
    FileInfo info = new FileInfoBuilder().path(path).owner(owner).group(group)
        .userPermissions(userRead, userWrite).groupPermissions(groupRead, groupWrite).build();
    assertEquals(path, info.getPath());
    assertEquals(owner, info.getOwner());
    assertEquals(group, info.getGroup());
    assertEquals(userRead, info.isUserRead());
    assertEquals(userWrite, info.isUserWrite());
    assertEquals(groupRead, info.isGroupRead());
    assertEquals(groupWrite, info.isGroupWrite());
  }

}
