package pl.edu.agh.kis.clientserverftp.server.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.SQLException;

import pl.edu.agh.kis.clientserverftp.server.ClientStatus;
import pl.edu.agh.kis.clientserverftp.server.DataSocket;
import pl.edu.agh.kis.clientserverftp.server.FtpResponses;
import pl.edu.agh.kis.clientserverftp.server.database.Database;
import pl.edu.agh.kis.clientserverftp.server.database.Registry;

/**
 * Task which saves file on server
 * 
 * @author Wojciech Kumoń
 *
 */
public class SaveFileTask implements StoppableTask {
  private static final StandardOpenOption DEFAULT_OPTION = StandardOpenOption.CREATE_NEW;
  private static final int BUFFER_SIZE = 8 * 1024;
  private ClientStatus status;
  private DataSocket dataSocket;
  private Database db;
  private Path fileFullPath;
  private StandardOpenOption openOption = DEFAULT_OPTION;
  private boolean stopped = false;

  /**
   * Constructs task
   * 
   * @param status client status
   * @param dataSocket data socket
   * @param db database
   * @param fileFullPath path to save file
   */
  public SaveFileTask(ClientStatus status, DataSocket dataSocket, Database db, Path fileFullPath) {
    this.status = status;
    this.dataSocket = dataSocket;
    this.db = db;
    this.fileFullPath = fileFullPath;
  }

  /**
   * Sets append mode
   * 
   * @param isAppendMode if true save task will be in append mode
   */
  public void setAppendMode(boolean isAppendMode) {
    if (isAppendMode) {
      openOption = StandardOpenOption.APPEND;
    } else {
      openOption = DEFAULT_OPTION;
    }
  }

  /**
   * Starts save file task
   */
  @Override
  public void run() {
    try (InputStream input = dataSocket.getSocket().getInputStream()) {
      try (OutputStream writer = Files.newOutputStream(fileFullPath, openOption)) {
        writeStreamToFile(input, writer);
      }
    } catch (IOException e) {
      status.log("IOException (probably socket closed)");
    } catch (InterruptedException e) {
      status.log("[ABORT] Thread: " + Thread.currentThread() + " interrupted");
      deleteFile();
      return;
    } finally {
      closeDataSocket();
    }
    addToDatabase(fileFullPath);
    status.getClientOut().println(FtpResponses.TRANSFER_COMPLETE_226);
    status.log(FtpResponses.TRANSFER_COMPLETE_226);
  }

  private void writeStreamToFile(InputStream input, OutputStream writer)
      throws InterruptedException, IOException {
    byte[] buffer = new byte[BUFFER_SIZE];
    int filled;
    while ((filled = input.read(buffer)) != -1) {
      checkIfStopped();
      writer.write(buffer, 0, filled);
      writer.flush();
    }
  }

  private void checkIfStopped() throws InterruptedException {
    if (stopped) {
      throw new InterruptedException();
    }
  }

  private void deleteFile() {
    try (Connection connection = Registry.createConnection()) {
      String relativePath = status.getFtpRelativePath(fileFullPath).toString();
      db.deleteFile(connection, relativePath);
      Files.deleteIfExists(fileFullPath);
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void closeDataSocket() {
    try {
      dataSocket.getSocket().close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void addToDatabase(Path fileFullPath) {
    try (Connection connection = Registry.createConnection()) {
      String relativePath = status.getFtpRelativePath(fileFullPath).toString();
      db.deleteFile(connection, relativePath);
      db.addFile(connection, relativePath, status.getUsername());
    } catch (SQLException e) {
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
