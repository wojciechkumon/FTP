package pl.edu.agh.kis.clientserverftp.client.controller;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Popup window to do action on ftp file.
 * @author Wojciech Kumoń
 *
 */
public class FtpFilePopup extends JPopupMenu {
  private static final long serialVersionUID = 4230100109392111319L;

  /**
   * Constructs and shows popup.
   * @param dirPath ftp file parent path
   * @param fileName ftp file name
   * @param treeModel ftp tree model
   * @param controller client controllers
   */
  public FtpFilePopup(String dirPath, String fileName, FtpSystemModel treeModel,
      ClientController controller) {
    JMenuItem delete = new JMenuItem("Delete");
    JMenuItem newDir = new JMenuItem("New directory");
    boolean isFile = isFile(treeModel, dirPath, fileName);
    delete.addActionListener(new DeleteListener(dirPath, fileName, isFile, controller));
    newDir.addActionListener(new NewDirectoryListener(dirPath, fileName, isFile, controller));
    add(delete);
    add(newDir);
  }

  private boolean isFile(FtpSystemModel treeModel, String dirPath, String fileName) {
    return treeModel.getNode(dirPath, fileName).isFile();
  }

}
