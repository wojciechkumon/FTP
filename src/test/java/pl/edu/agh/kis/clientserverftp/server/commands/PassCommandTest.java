package pl.edu.agh.kis.clientserverftp.server.commands;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pl.edu.agh.kis.clientserverftp.server.ClientStatus;
import pl.edu.agh.kis.clientserverftp.server.FtpResponses;
import pl.edu.agh.kis.clientserverftp.server.LoginStatus;
import pl.edu.agh.kis.clientserverftp.server.database.Database;
import pl.edu.agh.kis.clientserverftp.server.database.Registry;

public class PassCommandTest {
  private static String CORRECT_USER = "user";
  private static String CORRECT_PASSWORD = "password";
  private ClientStatus status;
  private Database db;

  @BeforeClass
  public static void init() throws SQLException {
    Registry.setConnectionData("jdbc:h2:~/test_db", "test", "test");
  }

  @AfterClass
  public static void clear() throws SQLException {
    try (Connection connection = Registry.createConnection()) {
      try (Statement stmt = connection.createStatement()) {
        stmt.executeUpdate("DROP ALL OBJECTS DELETE FILES");
      }
    }
    Registry.setDefaultConnectionData();
  }

  @Before
  public void initMocks() {
    status = mock(ClientStatus.class);
    db = mock(Database.class);
  }

  @Test
  public void testCorrectPassword() {
    when(db.isPasswordCorrect(any(), eq(CORRECT_USER), eq(CORRECT_PASSWORD))).thenReturn(true);
    when(status.getLastCommand()).thenReturn("USER " + CORRECT_USER);
    when(status.getLoginStatus()).thenReturn(LoginStatus.NOT_LOGGED_IN);
    Command passCommand = PassCommand.newInstance(status, "PASS " + CORRECT_PASSWORD, db);
    assertEquals(String.format(FtpResponses.USER_LOGGED_IN_230, CORRECT_USER),
        passCommand.execute());
  }

  @Test
  public void testWrongPassword() {
    String wrongPassword = "wrongPass";
    when(db.isPasswordCorrect(any(), eq(CORRECT_USER), eq(wrongPassword))).thenReturn(false);
    when(status.getLastCommand()).thenReturn("USER " + CORRECT_USER);
    when(status.getLoginStatus()).thenReturn(LoginStatus.NOT_LOGGED_IN);
    Command passCommand = PassCommand.newInstance(status, "PASS " + wrongPassword, db);
    assertEquals(FtpResponses.WRONG_PASSWORD_430, passCommand.execute());
  }

  @Test
  public void testWrongUsername() {
    String wrongUsername = "otherUser";
    when(db.isPasswordCorrect(any(), eq(wrongUsername), eq(CORRECT_PASSWORD))).thenReturn(false);
    when(status.getLastCommand()).thenReturn("USER " + wrongUsername);
    when(status.getLoginStatus()).thenReturn(LoginStatus.NOT_LOGGED_IN);
    Command passCommand = PassCommand.newInstance(status, "PASS " + CORRECT_PASSWORD, db);
    assertEquals(FtpResponses.WRONG_PASSWORD_430, passCommand.execute());
  }

  @Test
  public void testCommandWhenLastCommandNotUser() {
    when(db.isPasswordCorrect(any(), any(), any())).thenReturn(false);
    when(status.getLastCommand()).thenReturn("NOOP");
    when(status.getLoginStatus()).thenReturn(LoginStatus.NOT_LOGGED_IN);
    Command passCommand = PassCommand.newInstance(status, "PASS " + CORRECT_PASSWORD, db);
    assertEquals(FtpResponses.BAD_SEQUENCE_503, passCommand.execute());
  }

  @Test
  public void testPasswordCommandWhenLoggedIn() {
    when(status.getLastCommand()).thenReturn("USER " + CORRECT_USER);
    when(status.getLoginStatus()).thenReturn(LoginStatus.LOGGED_IN);
    Command passCommand = PassCommand.newInstance(status, "PASS " + CORRECT_PASSWORD, db);
    assertEquals(FtpResponses.YOU_ARE_LOGGED_IN_503, passCommand.execute());
  }
  
  @Test
  public void testPasswordWhenWithWrongCommand() {
    when(status.getLastCommand()).thenReturn("USER " + CORRECT_USER);
    when(status.getLoginStatus()).thenReturn(LoginStatus.NOT_LOGGED_IN);
    Command passCommand = PassCommand.newInstance(status, "PASS", db);
    assertEquals(FtpResponses.WRONG_PASSWORD_430, passCommand.execute());
  }

}
