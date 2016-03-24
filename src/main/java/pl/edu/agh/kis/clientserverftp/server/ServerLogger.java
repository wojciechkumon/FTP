package pl.edu.agh.kis.clientserverftp.server;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Allows logging to html files/
 * 
 * @author Wojciech Kumoń
 *
 */
public class ServerLogger implements Closeable {
  private static final Path DEFAULT_LOGS_DIRECTORY = getLogsDirectoryPath();
  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyy:MM:dd-HH_mm_ss");
  private static int ID_COUNTER = 0;
  private int id = ++ID_COUNTER;
  private Logger logger = Logger.getLogger(ServerLogger.class.getName() + id);

  /**
   * Constructs logger to save files in default directory from properties file.
   */
  public ServerLogger() {
    this(DEFAULT_LOGS_DIRECTORY);
  }

  /**
   * Constructs logger to save files in specified directory
   * @param logsDirectory directory to save logs
   */
  public ServerLogger(Path logsDirectory) {
    logger.setUseParentHandlers(false);
    logger.setLevel(Level.ALL);
    try {
      FileHandler fileHandler = new FileHandler(logsDirectory + "/" + getFileName());
      fileHandler.setFormatter(HtmlFormatter.getInstance());
      logger.addHandler(fileHandler);
    } catch (IOException e) {
      System.err.println("Can't add file handler to logger with id=" + id);
      e.printStackTrace();
    }
  }

  /**
   * Logs message
   * @param info message to log
   */
  public void log(String info) {
    logger.info(info);
  }

  /**
   * Close logger and all handlers.
   */
  @Override
  public void close() throws IOException {
    for (Handler handler : logger.getHandlers()) {
      handler.close();
    }
  }

  private String getFileName() {
    return "log" + "_" + LocalDateTime.now().format(FORMATTER) + "-" + id + ".html";
  }

  private static Path getLogsDirectoryPath() {
    return Paths.get(ServerProperties.getLogsDirectoryPath()).toAbsolutePath().normalize();
  }

}
