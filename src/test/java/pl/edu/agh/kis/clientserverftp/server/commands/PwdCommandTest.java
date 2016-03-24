package pl.edu.agh.kis.clientserverftp.server.commands;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.kis.clientserverftp.server.ClientStatus;
import pl.edu.agh.kis.clientserverftp.server.FtpResponses;
import pl.edu.agh.kis.clientserverftp.server.LoginStatus;

public class PwdCommandTest {
  private Path path;
  private ClientStatus status;

  @Before
  public void init() {
    path = Paths.get("dir/currentdir");
    status = mock(ClientStatus.class);
    when(status.getClientPath()).thenReturn(path);
  }

  @Test
  public void testWhenLoggedIn() {
    when(status.getLoginStatus()).thenReturn(LoginStatus.LOGGED_IN);
    Command pwdCommand = PwdCommand.newInstance(status);
    assertEquals("257 \"/" + path + "\" is current directory", pwdCommand.execute());
  }

  @Test
  public void testWhenNotLoggedIn() {
    when(status.getLoginStatus()).thenReturn(LoginStatus.NOT_LOGGED_IN);
    Command pwdCommand = PwdCommand.newInstance(status);
    assertEquals(FtpResponses.BAD_SEQUENCE_503, pwdCommand.execute());
  }

}
