package pl.edu.agh.kis.clientserverftp.server.commands;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import pl.edu.agh.kis.clientserverftp.server.DataSocket;
import pl.edu.agh.kis.clientserverftp.server.FtpResponses;
import pl.edu.agh.kis.clientserverftp.server.LoginStatus;

public class AborCommandTest {

  @Test
  public void testAborCommandIfLoggedIn() {
    DataSocket dataSocket = mock(DataSocket.class);
    AborCommand command = AborCommand.newInstance(LoginStatus.LOGGED_IN, dataSocket);
    assertEquals(FtpResponses.ABORT_SUCCESSFUL_226, command.execute());
    verify(dataSocket).stop();
  }
  
  @Test
  public void testAborCommandIfNotLoggedIn() {
    DataSocket dataSocket = mock(DataSocket.class);
    AborCommand command = AborCommand.newInstance(LoginStatus.NOT_LOGGED_IN, dataSocket);
    assertEquals(FtpResponses.BAD_SEQUENCE_503, command.execute());
  }

}
