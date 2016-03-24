package pl.edu.agh.kis.clientserverftp.client.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.SwingUtilities;

import pl.edu.agh.kis.clientserverftp.client.view.DownloadDialog;

/**
 * Get command, download file from server
 * 
 * @author Wojciech Kumoń
 *
 */
public class GetCommand implements FtpClientProtocolCommand {
  private String ftpPath;
  private String localPath;
  private ClientController controller;

  /**
   * Constructs command
   * 
   * @param ftpPath ftp file path
   * @param localPath directory path to save file
   * @param controller client controller
   */
  public GetCommand(String ftpPath, String localPath, ClientController controller) {
    this.ftpPath = ftpPath;
    this.localPath = localPath;
    this.controller = controller;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute() {
    String ftpFileName = Paths.get(ftpPath).getFileName().toString();
    Path pathToSave = controller.getMediator().getPathToSave(localPath, ftpFileName);
    Future<Boolean> future = getDownloadingTask(ftpPath, pathToSave.toString());
    SwingUtilities
        .invokeLater(() -> new DownloadDialog(controller, "Downloading...", future, pathToSave)
            .waitForTransfer());
  }

  private Future<Boolean> getDownloadingTask(String ftpPath, String localFilePath) {
    ServerMediator mediator = controller.getMediator();
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Callable<Boolean> callable = () -> mediator.downloadFile(ftpPath, localFilePath);
    return executor.submit(callable);
  }

}
