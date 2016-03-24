package pl.edu.agh.kis.clientserverftp.server.database;

import static org.junit.Assert.*;

import org.junit.Test;

import pl.edu.agh.kis.clientserverftp.server.database.DbUser;

public class DbUserTest {

  @Test
  public void testObject() {
    String username = "name";
    String password = "pass";
    String salt = "abcdefg";
    DbUser user = new DbUser(username, password, salt);
    assertEquals(username, user.getUsername());
    assertEquals(password, user.getPassword());
    assertEquals(salt, user.getSalt());
  }

  @Test
  public void testGettingRandomSalt() {
    assertNotEquals(DbUser.getRandomSalt(), DbUser.getRandomSalt());
  }

}
