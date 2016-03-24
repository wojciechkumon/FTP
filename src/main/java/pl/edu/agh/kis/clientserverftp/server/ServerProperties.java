package pl.edu.agh.kis.clientserverftp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Reads propperties file and hold them.
 * 
 * @author Wojciech Kumoń
 *
 */
public class ServerProperties {
  private static final String SERVER_ROOT_PATH_KEY = "ROOT_PATH";
  private static final String FTP_DIRECTORY_PATH_KEY = "FTP_PATH";
  private static final String DATABASE_DIRECTORY_PATH_KEY = "DB_PATH";
  private static final String LOGS_DIRECTORY_PATH_KEY = "LOGS_PATH";
  private static final String THREAD_POOL_SIZE_KEY = "THREAD_POOL_SIZE";
  private static URL propertiesFile = ClassLoader.getSystemResource("properties.txt");
  private static Properties properties;

  private static final int DEFAULT_THREAD_POOL_SIZE = 100;
  private static final String DEFAULT_ROOT_PATH = ".";
  
  /**
   * Ftp directory name.
   */
  public static final String FTP_DIRECTORY_NAME = "ftp_root";
  
  /**
   * Database directory name.
   */
  public static final String DATABASE_DIRECTORY_NAME = "database";
  
  /**
   * Logs directory name.
   */
  public static final String LOGS_DIRECTORY_NAME = "logs";

  static {
    reloadProperties(getDefaultProperties());
  }

  /**
   * Returns root directory path.
   * @return root directory path
   */
  public static String getRootDirectoryPath() {
    return properties.getProperty(SERVER_ROOT_PATH_KEY);
  }

  /**
   * Returns FTP directory path.
   * @return FTP directory path
   */
  public static String getFtpDirectoryPath() {
    return properties.getProperty(FTP_DIRECTORY_PATH_KEY);
  }

  /**
   * Returns database directory path.
   * @return database directory path
   */
  public static String getDatabaseDirectoryPath() {
    return properties.getProperty(DATABASE_DIRECTORY_PATH_KEY);
  }

  /**
   * Returns logs directory path.
   * @return logs directory path
   */
  public static String getLogsDirectoryPath() {
    return properties.getProperty(LOGS_DIRECTORY_PATH_KEY);
  }

  /**
   * Returns thread pool size.
   * @return thread pool size
   */
  public static int getThreadPoolSize() {
    return Integer.parseInt(properties.getProperty(THREAD_POOL_SIZE_KEY));
  }

  /**
   * Changes properties file and reloads them.
   * @param path Properties file path
   */
  public static void changePropertiesFile(URL path) {
    propertiesFile = path;
    reloadProperties(getDefaultProperties());
  }

  private static void reloadProperties(Properties defaultProperties) {
    properties = new Properties(defaultProperties);
    if (propertiesFile != null) {
      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(propertiesFile.openStream()))) {
        properties.load(reader);
      } catch (IOException e) {
        throw new PropertiesIOException(e);
      }
    }
    properties.setProperty(FTP_DIRECTORY_PATH_KEY, createFtpPath());
    properties.setProperty(DATABASE_DIRECTORY_PATH_KEY, createDbPath());
    properties.setProperty(LOGS_DIRECTORY_PATH_KEY, createLogsPath());
    createDirectoriesIfNotExists();
  }

  private static Properties getDefaultProperties() {
    Properties defaultProperties = new Properties();
    defaultProperties.setProperty(SERVER_ROOT_PATH_KEY, DEFAULT_ROOT_PATH);
    defaultProperties.setProperty(THREAD_POOL_SIZE_KEY, Integer.toString(DEFAULT_THREAD_POOL_SIZE));
    return defaultProperties;
  }

  private static String createFtpPath() {
    return properties.getProperty(SERVER_ROOT_PATH_KEY) + "/" + FTP_DIRECTORY_NAME;
  }

  private static String createDbPath() {
    return properties.getProperty(SERVER_ROOT_PATH_KEY) + "/" + DATABASE_DIRECTORY_NAME;
  }

  private static String createLogsPath() {
    return properties.getProperty(SERVER_ROOT_PATH_KEY) + "/" + LOGS_DIRECTORY_NAME;
  }

  private static void createDirectoriesIfNotExists() {
    Path rootPath = Paths.get(getRootDirectoryPath()).toAbsolutePath().normalize();
    createDirIfNoExists(rootPath);
    Path ftpPath = Paths.get(getFtpDirectoryPath()).toAbsolutePath().normalize();
    createDirIfNoExists(ftpPath);
    Path dbPath = Paths.get(getDatabaseDirectoryPath()).toAbsolutePath().normalize();
    createDirIfNoExists(dbPath);
    Path logsPath = Paths.get(getLogsDirectoryPath()).toAbsolutePath().normalize();
    createDirIfNoExists(logsPath);
  }

  private static void createDirIfNoExists(Path path) {
    try {
      if (!Files.exists(path) || !Files.isDirectory(path)) {
        Files.deleteIfExists(path);
        Files.createDirectory(path);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private ServerProperties() {}

}
