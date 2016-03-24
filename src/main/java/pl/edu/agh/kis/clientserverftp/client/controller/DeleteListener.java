package pl.edu.agh.kis.clientserverftp.client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Delete file/directory from server controller
 * @author Wojciech Kumoń
 *
 */
public class DeleteListener implements ActionListener {
  private boolean isFile;
  private String fullPath;
  private ClientController controller;

  /**
   * Contruct listener
   * @param dirPath file parent path on server
   * @param fileName file name on server
   * @param isFile true if is file, false otherwise
   * @param controller client controller
   */
  public DeleteListener(String dirPath, String fileName, boolean isFile,
      ClientController controller) {
    this.isFile = isFile;
    this.controller = controller;
    this.fullPath = dirPath + "/" + fileName;
  }

  /**
   * Delete file/directory from server
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    if (isFile) {
      controller.getMediator().sendAndGetResponse(FtpClientCommands.getDeleCommand(fullPath));
    } else {
      controller.getMediator().sendAndGetResponse(FtpClientCommands.getRmdCommand(fullPath));
    }
    controller.clearCache();
  }

}
