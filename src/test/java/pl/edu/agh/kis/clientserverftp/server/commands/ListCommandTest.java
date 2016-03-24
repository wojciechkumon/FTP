package pl.edu.agh.kis.clientserverftp.server.commands;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;

import pl.edu.agh.kis.clientserverftp.server.ClientStatus;
import pl.edu.agh.kis.clientserverftp.server.DataSocket;
import pl.edu.agh.kis.clientserverftp.server.FtpResponses;
import pl.edu.agh.kis.clientserverftp.server.LoginStatus;

public class ListCommandTest {

  @Test
  public void testListCommandWhenNotLoggedIn() {
    ClientStatus status = mock(ClientStatus.class);
    when(status.getLoginStatus()).thenReturn(LoginStatus.NOT_LOGGED_IN);
    DataSocket dataSocket = mock(DataSocket.class);
    
    Command listCommand = ListCommand.newInstance(status, dataSocket);
    assertEquals(FtpResponses.BAD_SEQUENCE_503, listCommand.execute());
  }
  
  @Test
  public void testWhenDataConnectionNotSet() {
    ClientStatus status = mock(ClientStatus.class);
    when(status.getLoginStatus()).thenReturn(LoginStatus.LOGGED_IN);
    DataSocket dataSocket = mock(DataSocket.class);
    when(dataSocket.isDataConnectionSet()).thenReturn(false);
    
    Command listCommand = ListCommand.newInstance(status, dataSocket);
    assertEquals(FtpResponses.USE_PORT_OR_PASV_425, listCommand.execute());
  }
  
  @Test
  public void testWhenActiveModeIsSetAndCantOpenConnection() throws IOException {
    ClientStatus status = mock(ClientStatus.class);
    when(status.getLoginStatus()).thenReturn(LoginStatus.LOGGED_IN);
    DataSocket dataSocket = mock(DataSocket.class);
    when(dataSocket.isDataConnectionSet()).thenReturn(true);
    when(dataSocket.isActiveModeSet()).thenReturn(true);
    doThrow(new IOException()).when(dataSocket).connectActive();
    
    Command listCommand = ListCommand.newInstance(status, dataSocket);
    assertEquals(FtpResponses.CANT_OPEN_DATA_CONNECTION_425, listCommand.execute());
  }
  
  @Test
  public void testWhenActiveModeIsSet() {
    ClientStatus status = mock(ClientStatus.class);
    when(status.getLoginStatus()).thenReturn(LoginStatus.LOGGED_IN);
    DataSocket dataSocket = mock(DataSocket.class);
    when(dataSocket.isDataConnectionSet()).thenReturn(true);
    when(dataSocket.isActiveModeSet()).thenReturn(true);
    
    Command listCommand = ListCommand.newInstance(status, dataSocket);
    assertEquals(FtpResponses.ASCII_CONNECTION_LS_150, listCommand.execute());
    verify(dataSocket).startThread(any());
  }
  
  @Test
  public void testWhenPassiveModeIsSet() {
    ClientStatus status = mock(ClientStatus.class);
    when(status.getLoginStatus()).thenReturn(LoginStatus.LOGGED_IN);
    DataSocket dataSocket = mock(DataSocket.class);
    when(dataSocket.isDataConnectionSet()).thenReturn(true);
    when(dataSocket.isActiveModeSet()).thenReturn(false);
    
    Command listCommand = ListCommand.newInstance(status, dataSocket);
    assertEquals(FtpResponses.ASCII_CONNECTION_LS_150, listCommand.execute());
    verify(dataSocket).startThread(any());
  }
  
  

}
