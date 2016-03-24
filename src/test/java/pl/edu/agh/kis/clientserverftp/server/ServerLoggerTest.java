package pl.edu.agh.kis.clientserverftp.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ServerLoggerTest {
  private static final String MESSAGE = "message";
  private static final Path TEST_DIR = Paths.get("loggerTestDir").toAbsolutePath();

  @BeforeClass
  public static void init() throws IOException {
    Files.createDirectory(TEST_DIR);
  }

  @AfterClass
  public static void cleanUp() throws IOException {
    try (DirectoryStream<Path> dir = Files.newDirectoryStream(TEST_DIR)) {
      for (Path path : dir) {
        Files.delete(path);
      }
    }
    Files.delete(TEST_DIR);
  }


  @Test
  public void test() throws Exception {
    try (ServerLogger logger = new ServerLogger(TEST_DIR)) {
      logger.log(MESSAGE);
    }
    Path logFile = getLog();
    String extension = com.google.common.io.Files.getFileExtension(logFile.toString());
    assertEquals("html", extension);
    String logText = getLogText(logFile);
    assertTrue(logText.contains(MESSAGE));
  }

  private Path getLog() throws IOException {
    try (DirectoryStream<Path> dir = Files.newDirectoryStream(TEST_DIR)) {
      return dir.iterator().next();
    }
  }

  private String getLogText(Path logFile) throws IOException {
    StringBuilder builder = new StringBuilder();
    try (BufferedReader reader = Files.newBufferedReader(logFile)) {
      String line;
      while ((line = reader.readLine()) != null) {
        builder.append(line);
      }
    }
    return builder.toString();
  }

}
