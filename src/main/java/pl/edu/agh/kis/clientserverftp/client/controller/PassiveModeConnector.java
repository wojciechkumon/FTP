package pl.edu.agh.kis.clientserverftp.client.controller;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Connects with server in passive mode.
 * @author Wojciech Kumoń
 *
 */
public class PassiveModeConnector implements Closeable {
  private static final int BUFFER_SIZE = 8 * 1024;
  private Socket server;

  /**
   * Connects with host
   * @param host host to connect with
   * @param port port
   * @throws IOException IOException may be thrown while connecting
   */
  public PassiveModeConnector(String host, int port) throws IOException {
    server = new Socket();
    server.connect(new InetSocketAddress(host, port));
  }

  /**
   * Puts all input to file
   * @param pathToSave file path
   */
  public void saveInputToFile(Path pathToSave) {
    try (OutputStream writer = Files.newOutputStream(pathToSave)) {
      try (InputStream reader = server.getInputStream()) {
        readAndWriteAll(reader, writer);
      }
    } catch (IOException e) {
      System.out.println("End of transfer " + e.toString());
    }
  }

  private void readAndWriteAll(InputStream reader, OutputStream writer) throws IOException {
    byte[] buffer = new byte[BUFFER_SIZE];
    int filled;
    while ((filled = reader.read(buffer)) != -1) {
      writer.write(buffer, 0, filled);
      writer.flush();
    }
  }

  /**
   * Puts file to server
   * @param pathToSend file path to send
   */
  public void sendFileToServer(Path pathToSend) {
    try (InputStream reader = Files.newInputStream(pathToSend)) {
      try (OutputStream writer = server.getOutputStream()) {
        readAndWriteAll(reader, writer);
      }
    } catch (IOException e) {
      System.out.println("End of transfer " + e.toString());
    }
  }

  /**
   * Returns all input as a string
   * @return all input as a string
   */
  public String getAllInput() {
    StringBuilder builder = new StringBuilder();
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(server.getInputStream()))) {
      saveDataToStringBuilder(builder, reader);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return builder.toString();
  }

  private void saveDataToStringBuilder(StringBuilder builder, BufferedReader reader)
      throws IOException {
    char[] buffer = new char[BUFFER_SIZE];
    int filled;
    while ((filled = reader.read(buffer)) != -1) {
      builder.append(buffer, 0, filled);
    }
  }

  /**
   * Close connector.
   */
  @Override
  public void close() throws IOException {
    server.close();
  }


}
