package pl.edu.agh.kis.clientserverftp.server.commands;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pl.edu.agh.kis.clientserverftp.server.FtpResponses;
import pl.edu.agh.kis.clientserverftp.server.LoginStatus;

public class UserCommandTest {
  private static String USERNAME = "user";

  @Test
  public void testUserCommandWhenNotLoggedIn() {
    Command userCommand = UserCommand.newInstance(LoginStatus.NOT_LOGGED_IN, "USER " + USERNAME);
    String correctResponse = String.format(FtpResponses.PASSWORD_REQUIRED_331, USERNAME);
    assertEquals(correctResponse, userCommand.execute());
  }

  @Test
  public void testUserCommandWhenLoggedIn() {
    Command userCommand = UserCommand.newInstance(LoginStatus.LOGGED_IN, "USER " + USERNAME);
    String correctResponse = FtpResponses.YOU_ARE_LOGGED_IN_503;
    assertEquals(correctResponse, userCommand.execute());
  }

  @Test
  public void testWithWrongCommandFormat() {
    Command userCommand = UserCommand.newInstance(LoginStatus.NOT_LOGGED_IN, "USER");
    String correctResponse = String.format(FtpResponses.PASSWORD_REQUIRED_331, "");
    assertEquals(correctResponse, userCommand.execute());
  }

}
