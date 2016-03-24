package pl.edu.agh.kis.clientserverftp.server;

import static org.junit.Assert.*;

import org.junit.Test;

public class LoginStatusTest {

  @Test
  public void test() {
    assertEquals(2, LoginStatus.values().length);
    assertEquals(LoginStatus.LOGGED_IN, LoginStatus.valueOf("LOGGED_IN"));
    assertEquals(LoginStatus.NOT_LOGGED_IN, LoginStatus.valueOf("NOT_LOGGED_IN"));
  }

}
