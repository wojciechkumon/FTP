package pl.edu.agh.kis.clientserverftp.client.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

/**
 * Ftp files mouse listener. Creates popup on right mouse button click.
 * @author Wojciech Kumoń
 *
 */
public class FtpFileTreeMouseListener implements MouseListener {
  private ClientController controller;

  /**
   * Creates listener
   * @param controller client controller
   */
  public FtpFileTreeMouseListener(ClientController controller) {
    this.controller = controller;
  }

  /**
   * Creates popup on right mouse button click
   */
  @Override
  public void mouseClicked(MouseEvent e) {
    if (SwingUtilities.isRightMouseButton(e)) {
      JTree tree = (JTree) e.getSource();
      int row = tree.getClosestRowForLocation(e.getX(), e.getY());
      tree.setSelectionRow(row);
      TreePath treePath = tree.getPathForRow(row);
      String dirPath = getDirPath(treePath);
      String fileName = getFileName(treePath);
      FtpFilePopup popup =
          new FtpFilePopup(dirPath, fileName, (FtpSystemModel) tree.getModel(), controller);
      popup.show(tree, e.getX(), e.getY());
    }
  }

  private String getDirPath(TreePath pathForRow) {
    int numberOfElements = pathForRow.getPathCount();
    StringBuilder builder = new StringBuilder();
    if (numberOfElements <= 1) {
      return "/";
    }
    for (int i = 0; i < numberOfElements - 1; i++) {
      builder.append(pathForRow.getPathComponent(i));
      builder.append('/');
    }
    return builder.substring(0, builder.length() - 1);
  }

  private String getFileName(TreePath pathForRow) {
    int numberOfElements = pathForRow.getPathCount();
    return pathForRow.getPathComponent(numberOfElements - 1).toString();
  }

  /**
   * does nothing
   * @param e event
   */
  @Override
  public void mouseEntered(MouseEvent e) {}

  /**
   * does nothing
   * @param e event
   */
  @Override
  public void mouseExited(MouseEvent e) {}

  /**
   * does nothing
   * @param e event
   */
  @Override
  public void mousePressed(MouseEvent e) {}

  /**
   * does nothing
   * @param e event
   */
  @Override
  public void mouseReleased(MouseEvent e) {}

}
