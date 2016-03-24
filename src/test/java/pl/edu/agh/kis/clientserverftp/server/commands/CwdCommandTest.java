package pl.edu.agh.kis.clientserverftp.server.commands;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import pl.edu.agh.kis.clientserverftp.server.ClientStatus;
import pl.edu.agh.kis.clientserverftp.server.FtpResponses;
import pl.edu.agh.kis.clientserverftp.server.LoginStatus;
import pl.edu.agh.kis.clientserverftp.server.database.Database;
import pl.edu.agh.kis.clientserverftp.server.database.FileInfo.FileInfoBuilder;
import pl.edu.agh.kis.clientserverftp.server.database.Registry;

@RunWith(PowerMockRunner.class)
public class CwdCommandTest {
  
  private static final String EXISTING_DIR = "dir";
  private static final String NOT_EXISTING_DIR = "wrong_dir";
  
  @BeforeClass
  public static void init() throws IOException {
    Files.createDirectory(Paths.get(EXISTING_DIR));
  }
  
  @AfterClass
  public static void cleanUp() throws IOException {
    Files.deleteIfExists(Paths.get(EXISTING_DIR));
  }

  @Test
  public void testWhenNotLoggedIn() {
    ClientStatus status = mock(ClientStatus.class);
    when(status.getLoginStatus()).thenReturn(LoginStatus.NOT_LOGGED_IN);
    Command cwd = CwdCommand.newInstance(status, "", mock(Database.class));
    assertEquals(FtpResponses.BAD_SEQUENCE_503, cwd.execute());
  }
  
  @PrepareForTest({Registry.class})
  @Test
  public void testWhenLoggedInAndDirExistsInDatabase() throws IOException { 
    mockStatic(Registry.class, x -> null);
    ClientStatus status = mock(ClientStatus.class);
    when(status.getLoginStatus()).thenReturn(LoginStatus.LOGGED_IN);
    when(status.getAbsoluteResolvedPath(Mockito.any())).thenReturn(Paths.get(EXISTING_DIR));
    when(status.getFtpRelativePath(Mockito.any())).thenReturn(Paths.get(EXISTING_DIR));
    Database db =  mock(Database.class);
    when(db.getFileInfo(Mockito.any(), Mockito.any())).thenReturn(new FileInfoBuilder().build());
    when(db.getUserId(Mockito.any(Connection.class), Mockito.anyString())).thenReturn(1);
    when(db.canRead(Mockito.any(Connection.class), Mockito.anyInt(), Mockito.anyString())).thenReturn(true);
    
    Command cwd = CwdCommand.newInstance(status, EXISTING_DIR, db);
    assertEquals(FtpResponses.CWD_SUCCESFUL_250, cwd.execute());
  }
  
  @PrepareForTest({Registry.class})
  @Test
  public void testWhenLoggedInAndDirNotExistsInDatabase() throws IOException { 
    mockStatic(Registry.class, x -> null);
    ClientStatus status = mock(ClientStatus.class);
    when(status.getLoginStatus()).thenReturn(LoginStatus.LOGGED_IN);
    when(status.getAbsoluteResolvedPath(Mockito.any())).thenReturn(Paths.get(NOT_EXISTING_DIR));
    when(status.getFtpRelativePath(Mockito.any())).thenReturn(Paths.get(NOT_EXISTING_DIR));
    Database db =  mock(Database.class);
    when(db.getFileInfo(Mockito.any(), Mockito.any())).thenReturn(null);
    
    Command cwd = CwdCommand.newInstance(status, EXISTING_DIR, db);
    assertEquals(FtpResponses.NO_SUCH_FILE_OR_DIRECTORY_550, cwd.execute());
  }

}
