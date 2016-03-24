package pl.edu.agh.kis.clientserverftp.client.controller;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.SwingUtilities;

import pl.edu.agh.kis.clientserverftp.client.view.DownloadDialog;

/**
 * Command which upload file to server
 * @author Wojciech Kumoń
 *
 */
public class PutCommand implements FtpClientProtocolCommand {
  private String localPath;
  private String ftpPath;
  private ClientController controller;

  /**
   * Construct command
   * @param localPath file path to upload
   * @param ftpPath ftp path to save file
   * @param controller client controller
   */
  public PutCommand(String localPath, String ftpPath, ClientController controller) {
    this.localPath = localPath;
    this.ftpPath = ftpPath;
    this.controller = controller;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute() {
    Future<Boolean> future = getUploadingTask(localPath, ftpPath);
    SwingUtilities.invokeLater(
        () -> new DownloadDialog(controller, "Uploading...", future).waitForTransfer());
  }

  private Future<Boolean> getUploadingTask(String localFilePath, String ftpFullPath) {
    ServerMediator mediator = controller.getMediator();
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Callable<Boolean> callable = () -> mediator.uploadFile(ftpFullPath, localFilePath);
    return executor.submit(callable);
  }

}
