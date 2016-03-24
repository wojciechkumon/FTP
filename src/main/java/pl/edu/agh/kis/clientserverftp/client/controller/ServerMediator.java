package pl.edu.agh.kis.clientserverftp.client.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Server mediator, allows to simply send command and get response. controller
 * 
 * @author Wojciech Kumoń
 *
 */
public final class ServerMediator {
  private ClientController controller;

  /**
   * Constructs mediator.
   * 
   * @param controller client controller.
   */
  public ServerMediator(ClientController controller) {
    this.controller = controller;
  }

  /**
   * Send message and return response
   * 
   * @param command command to send
   * @return response from server
   */
  public String sendAndGetResponse(String command) {
    controller.sendToServer(command);
    return controller.getLineFromServer();
  }

  /**
   * Send list command and return response.
   * 
   * @return list command response
   */
  public String getListResponse() {
    try (PassiveModeConnector connector = getConnector()) {
      String response;
      boolean correctResponse;
      do {
        controller.sendToServer(FtpClientCommands.getListCommand());
        response = controller.getLineFromServer();
        correctResponse = response.startsWith("1") || response.startsWith("2");
        if (!correctResponse) {
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      } while (!correctResponse);
      String serverOutput = connector.getAllInput();
      response = controller.getLineFromServer();
      if (response.startsWith("4") || response.startsWith("5")) {
        return "";
      }
      return serverOutput;
    } catch (IOException | ConnectionException e) {
      return "";
    }
  }

  private PassiveModeConnector getConnector() throws IOException, ConnectionException {
    controller.sendToServer(FtpClientCommands.getPasvCommand());
    String response = controller.getLineFromServer();
    if (response.startsWith("4") || response.startsWith("5")) {
      throw new ConnectionException();
    }
    String ip;
    int port;
    try {
      ip = getIpFromResponse(response);
      port = getPortFromResponse(response);
    } catch (NumberFormatException e) {
      throw new ConnectionException(e);
    }
    return new PassiveModeConnector(ip, port);
  }

  private String getIpFromResponse(String response) {
    String[] splittedData = getSplittedIpAndPort(response);
    StringBuilder ip = new StringBuilder(splittedData[0]);
    for (int i = 1; i <= 3; ++i) {
      ip.append(".");
      ip.append(splittedData[i]);
    }
    return ip.toString();
  }

  private int getPortFromResponse(String response) {
    String[] splittedData = getSplittedIpAndPort(response);
    return Short.parseShort(splittedData[4]) * 256 + Short.parseShort(splittedData[5]);
  }

  private String[] getSplittedIpAndPort(String response) {
    int indexOfOpeningBracket = response.indexOf("(");
    int indexOfClosingBracket = response.indexOf(")");
    if (indexOfOpeningBracket == -1 || indexOfClosingBracket == -1) {
      throw new NumberFormatException();
    }
    String ipPortString = response.substring(indexOfOpeningBracket + 1, indexOfClosingBracket);
    String[] splitted = ipPortString.split(",");
    if (splitted.length != 6) {
      throw new NumberFormatException();
    }
    return splitted;
  }

  /**
   * Download file from server. Method launch passive mode and download file.
   * 
   * @param ftpPath ftp file path to download
   * @param localPath path to save there file
   * @return true if download was completed, false otherwise
   */
  public boolean downloadFile(String ftpPath, String localPathToSave) {
    Path pathToSave = Paths.get(localPathToSave);
    try (PassiveModeConnector connector = getConnector()) {
      String response = sendAndGetResponse(FtpClientCommands.getRetrCommand(ftpPath));
      if (response.startsWith("4") || response.startsWith("5")) {
        return false;
      }
      connector.saveInputToFile(pathToSave);
      response = controller.getLineFromServer();
      if (response.startsWith("4") || response.startsWith("5")) {
        return false;
      }
      controller.refreshLocalFiles();
      return true;
    } catch (IOException | ConnectionException e) {
      return false;
    }
  }

  public Path getPathToSave(String localPath, String ftpFileName) {
    Path dirToSave = Paths.get(localPath);
    Path pathToSave = dirToSave.resolve(ftpFileName);
    String fileNameToSave = ftpFileName;

    int i = 1;
    while (Files.exists(pathToSave)) {
      fileNameToSave = com.google.common.io.Files.getNameWithoutExtension(ftpFileName) + i++ + "."
          + com.google.common.io.Files.getFileExtension(ftpFileName);
      pathToSave = dirToSave.resolve(fileNameToSave);
    }
    return pathToSave;
  }

  /**
   * Upload file to server. Method launch passive mode and upload file.
   * 
   * @param ftpDir ftp directory to save there file
   * @param localPath file path to send
   * @return true if upload was completed, false otherwise
   */
  public boolean uploadFile(String ftpDir, String localPath) {
    try (PassiveModeConnector connector = getConnector()) {
      String response = sendAndGetResponse(FtpClientCommands.getStorCommand(ftpDir));
      if (response.startsWith("4") || response.startsWith("5")) {
        return false;
      }
      connector.sendFileToServer(Paths.get(localPath));
      response = controller.getLineFromServer();
      if (response.startsWith("4") || response.startsWith("5")) {
        return false;
      }
      controller.clearCache();
      return true;
    } catch (IOException | ConnectionException e) {
      return false;
    }
  }

  /**
   * Set binary mode on server. Send binary mode FTP command.
   */
  public void setBinaryMode() {
    sendAndGetResponse(FtpClientCommands.getBinaryModeCommand());
  }

}
