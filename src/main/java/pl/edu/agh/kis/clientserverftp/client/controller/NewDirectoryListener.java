package pl.edu.agh.kis.clientserverftp.client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

/**
 * Handles creating directories on server
 * @author Wojciech Kumoń
 */
public class NewDirectoryListener implements ActionListener {
  private String dirPath;
  private ClientController controller;
  private boolean isFile;
  private String fileName;

  /**
   * Listener contructor
   * @param dirPath clicked file parent path
   * @param fileName clicked file name
   * @param isFile true if is file, false otherwise
   * @param controller client controller
   */
  public NewDirectoryListener(String dirPath, String fileName, boolean isFile,
      ClientController controller) {
    this.dirPath = dirPath;
    this.fileName = fileName;
    this.isFile = isFile;
    this.controller = controller;
  }

  /**
   * Creates new directory. If directory was clicked, new directory will be it's child.
   * If file was clicked, new directory will be child of this file parent.
   * @param e event
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    String directoryName = JOptionPane.showInputDialog(controller.getView(), "New directory name:",
        "Creating directory", JOptionPane.QUESTION_MESSAGE);
    if (directoryName == null || directoryName.isEmpty()) {
      return;
    }
    if (dirPath.equals("/")) {
      controller.getMediator()
          .sendAndGetResponse(FtpClientCommands.getMkdCommand(dirPath + directoryName));
    } else if (isFile) {
      controller.getMediator()
          .sendAndGetResponse(FtpClientCommands.getMkdCommand(dirPath + "/" + directoryName));
    } else {
      controller.getMediator().sendAndGetResponse(
          FtpClientCommands.getMkdCommand(dirPath + "/" + fileName + "/" + directoryName));
    }
    controller.clearCache();
  }

}
