package pl.edu.agh.kis.clientserverftp.server.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import pl.edu.agh.kis.clientserverftp.server.ClientStatus;
import pl.edu.agh.kis.clientserverftp.server.DataSocket;
import pl.edu.agh.kis.clientserverftp.server.FtpResponses;

/**
 * Task which sends file from server
 * @author Wojciech Kumoń
 *
 */
public class SendFileTask implements StoppableTask {
  private static final int BUFFER_SIZE = 8 * 1024;
  private ClientStatus status;
  private DataSocket dataSocket;
  private Path fileFullPath;
  private boolean stopped = false;

  /**
   * Constructs task
   * @param status client status
   * @param dataSocket data socket
   * @param fileFullPath file to send
   */
  public SendFileTask(ClientStatus status, DataSocket dataSocket, Path fileFullPath) {
    this.status = status;
    this.dataSocket = dataSocket;
    this.fileFullPath = fileFullPath;
  }

  /**
   * Starts sending file.
   */
  @Override
  public void run() {
    try (InputStream reader = Files.newInputStream(fileFullPath)) {
      try (OutputStream out = dataSocket.getSocket().getOutputStream()) {
        sendFileToStream(reader, out);
      }
    } catch (IOException e) {
      status.log("IOException (probably socket closed)");
    } catch (InterruptedException e) {
      status.log("[ABORT] Thread: " + Thread.currentThread() + " interrupted");
      return;
    } finally {
      closeDataSocket();
    }
    status.getClientOut().println(FtpResponses.TRANSFER_COMPLETE_226);
    status.log(FtpResponses.TRANSFER_COMPLETE_226);
  }

  private void sendFileToStream(InputStream reader, OutputStream out)
      throws IOException, InterruptedException {
    byte[] buffer = new byte[BUFFER_SIZE];
    int filled;
    while ((filled = reader.read(buffer)) != -1) {
      checkIfStopped();
      out.write(buffer, 0, filled);
      out.flush();
    }
  }

  private void checkIfStopped() throws InterruptedException {
    if (stopped) {
      throw new InterruptedException();
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
