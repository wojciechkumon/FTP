package pl.edu.agh.kis.clientserverftp.server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ServerPropertiesTest {
  private static Path testPropertiesFile = Paths.get("testProperties.test");
  private static final String ROOT_PATH = "./dir";
  private static final int THREAD_POOL_SIZE = 500;

  @BeforeClass
  public static void init() throws IOException {
    try (PrintWriter writer = new PrintWriter(
        Files.newBufferedWriter(testPropertiesFile, StandardOpenOption.CREATE_NEW))) {
      writer.println("ROOT_PATH=" + ROOT_PATH);
      writer.println("THREAD_POOL_SIZE=" + THREAD_POOL_SIZE);
    }
  }

  @AfterClass
  public static void cleanUp() throws IOException {
    Files.delete(testPropertiesFile);
    Files.deleteIfExists(Paths.get(ROOT_PATH).resolve(ServerProperties.FTP_DIRECTORY_NAME));
    Files.deleteIfExists(Paths.get(ROOT_PATH).resolve(ServerProperties.DATABASE_DIRECTORY_NAME));
    Files.deleteIfExists(Paths.get(ROOT_PATH).resolve(ServerProperties.LOGS_DIRECTORY_NAME));
    Files.deleteIfExists(Paths.get(ROOT_PATH));
  }

  @Test
  public void testOwnProperties() throws MalformedURLException {
    ServerProperties.changePropertiesFile(testPropertiesFile.toUri().toURL());
    assertEquals(ROOT_PATH, ServerProperties.getRootDirectoryPath());
    
    String expectedFtpDirPath = ROOT_PATH + "/" + ServerProperties.FTP_DIRECTORY_NAME;
    assertEquals(expectedFtpDirPath, ServerProperties.getFtpDirectoryPath());
    
    String expectedDbDirPath = ROOT_PATH + "/" + ServerProperties.DATABASE_DIRECTORY_NAME;
    assertEquals(expectedDbDirPath, ServerProperties.getDatabaseDirectoryPath());
    
    String expectedLogsDirPath = ROOT_PATH + "/" + ServerProperties.LOGS_DIRECTORY_NAME;
    assertEquals(expectedLogsDirPath, ServerProperties.getLogsDirectoryPath());
    assertEquals(THREAD_POOL_SIZE, ServerProperties.getThreadPoolSize());
  }

  @Test(expected = PropertiesIOException.class)
  public void testSettingNotExistingProperties() throws MalformedURLException {
    String url = "file:/notExistingProperties.test";
    ServerProperties.changePropertiesFile(new URL(url));
  }

}
