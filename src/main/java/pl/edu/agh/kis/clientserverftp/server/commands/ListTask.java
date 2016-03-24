package pl.edu.agh.kis.clientserverftp.server.commands;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.SystemUtils;

import pl.edu.agh.kis.clientserverftp.server.ClientStatus;
import pl.edu.agh.kis.clientserverftp.server.DataSocket;
import pl.edu.agh.kis.clientserverftp.server.FtpResponses;
import pl.edu.agh.kis.clientserverftp.server.ServerProperties;
import pl.edu.agh.kis.clientserverftp.server.database.Database;
import pl.edu.agh.kis.clientserverftp.server.database.FileInfo;
import pl.edu.agh.kis.clientserverftp.server.database.Registry;

/**
 * Task which lists ftp files
 * @author Wojciech Kumoń
 *
 */
public class ListTask implements StoppableTask {
  private static final String LIST_FILE_FORMAT = "%s %d %-10s %-10s %10d %s %s";
  private static final DateFormat FORMATTER = new SimpleDateFormat("MMM dd yyyy");
  private ClientStatus status;
  private DataSocket dataSocket;
  private boolean stopped = false;

  /**
   * Constructs task
   * @param status client status
   * @param dataSocket data socket
   */
  public ListTask(ClientStatus status, DataSocket dataSocket) {
    this.status = status;
    this.dataSocket = dataSocket;
  }

  /**
   * Runs list task, prints output to socket from ClientStatus
   */
  @Override
  public void run() {
    try (PrintStream writer =
        new PrintStream(new BufferedOutputStream(dataSocket.getSocket().getOutputStream()), true)) {
      printFiles(writer);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      status.log("Thread: " + Thread.currentThread() + " interrupted");
      return;
    } finally {
      closeDataSocket();
    }
  }

  private void printFiles(PrintStream writer) throws IOException, InterruptedException {
    Path rootPath = Paths.get(ServerProperties.getFtpDirectoryPath()).toRealPath();
    Connection connection = null;
    try (DirectoryStream<Path> directory = Files.newDirectoryStream(status.getRealPath())) {
      writer.println(getSpecialDirsString());
      connection = Registry.createConnection();
      for (Path dirElement : directory) {
        checkIfStopped();
        printElement(writer, rootPath, connection, dirElement);
      }
      status.getClientOut().println(FtpResponses.TRANSFER_COMPLETE_226);
      status.log(FtpResponses.TRANSFER_COMPLETE_226);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      closeConnection(connection);
    }
  }

  private void checkIfStopped() throws InterruptedException {
    if (stopped) {
      throw new InterruptedException();
    }
  }

  private void printElement(PrintStream writer, Path rootPath, Connection connection,
      Path dirElement) throws IOException {
    FileInfo info =
        Database.getInstance().getFileInfo(connection, rootPath.relativize(dirElement).toString());
    if (info != null) {
      writer.println(getFileDataString(dirElement, info));
    }
  }

  private String getSpecialDirsString() throws IOException {
    String permissions = "drwxrwxr-x";
    int links = 1;
    String username = "root";
    String groupName = "admin";
    int size = 4096;
    StringBuilder builder = new StringBuilder();
    builder.append(String.format(LIST_FILE_FORMAT, permissions, links, username, groupName, size,
        "Jan 1 2016", "."));
    builder.append("\r\n");
    builder.append(String.format(LIST_FILE_FORMAT, permissions, links, username, groupName, size,
        "Jan 1 2016", ".."));
    return builder.toString();
  }

  private String getFileDataString(Path dirElement, FileInfo info) throws IOException {
    String permissions = getPermisions(Files.isDirectory(dirElement), info);
    int links = getLinks(dirElement);
    String username = info.getOwner();
    String groupName = info.getGroup();
    long size = Files.size(dirElement);
    String fileName = dirElement.getFileName().toString();
    String date = FORMATTER.format(Files.getLastModifiedTime(dirElement).toMillis());
    return String.format(LIST_FILE_FORMAT, permissions, links, username, groupName, size, date, fileName);
  }

  private int getLinks(Path dirElement) throws IOException {
    if (SystemUtils.IS_OS_UNIX) {
      return (int) Files.getAttribute(dirElement, "unix:nlink");
    }
    return 1;
  }

  private String getPermisions(boolean isDirectory, FileInfo info) {
    StringBuilder builder = new StringBuilder();
    appendPermission(builder, 'd', isDirectory);
    builder.append("rwx");
    appendPermission(builder, 'r', info.isGroupRead());
    appendPermission(builder, 'w', info.isGroupWrite());
    builder.append('x');
    appendPermission(builder, 'r', info.isUserRead());
    appendPermission(builder, 'w', info.isUserWrite());
    builder.append('x');
    return builder.toString();
  }

  private void appendPermission(StringBuilder builder, char permissionChar, boolean isTrue) {
    if (isTrue) {
      builder.append(permissionChar);
    } else {
      builder.append('-');
    }
  }

  private void closeConnection(Connection connection) {
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  private void closeDataSocket() {
    try {
      dataSocket.getSocket().close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void stop() {
    stopped = true;
  }

}
