package pl.edu.agh.kis.clientserverftp.client.controller;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * Controlls drop on ftp files tree.
 * @author Wojciech Kumoń
 *
 */
public class FtpTreeDropTarget implements DropTargetListener {
  private JTree targetTree;
  private ClientController controller;

  /**
   * Constructs drop target
   * @param tree tree to control
   * @param controller client controller
   */
  public FtpTreeDropTarget(JTree tree, ClientController controller) {
    targetTree = tree;
    this.controller = controller;
    new DropTarget(targetTree, this);
  }

  private FtpTreeFile getNodeForEvent(DropTargetDragEvent dragEvent) {
    Point point = dragEvent.getLocation();
    DropTargetContext targetContext = dragEvent.getDropTargetContext();
    JTree tree = (JTree) targetContext.getComponent();
    TreePath path = tree.getClosestPathForLocation(point.x, point.y);
    return (FtpTreeFile) path.getLastPathComponent();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dragEnter(DropTargetDragEvent dragEvent) {
    FtpTreeFile node = getNodeForEvent(dragEvent);
    if (node.isFile()) {
      dragEvent.rejectDrag();
    } else {
      dragEvent.acceptDrag(dragEvent.getDropAction());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dragOver(DropTargetDragEvent dragEvent) {
    FtpTreeFile node = getNodeForEvent(dragEvent);
    if (node.isFile()) {
      dragEvent.rejectDrag();
    } else {
      dragEvent.acceptDrag(dragEvent.getDropAction());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dragExit(DropTargetEvent dragEvent) {}

  /**
   * {@inheritDoc}
   */
  @Override
  public void dropActionChanged(DropTargetDragEvent dragEvent) {}

  /**
   * {@inheritDoc}
   */
  @Override
  public void drop(DropTargetDropEvent dragEvent) {
    Point point = dragEvent.getLocation();
    TreePath parentpath = targetTree.getClosestPathForLocation(point.x, point.y);
    FtpTreeFile ftpDir = (FtpTreeFile) parentpath.getLastPathComponent();
    if (ftpDir.isFile()) {
      dragEvent.rejectDrop();
      return;
    }

    try {
      Transferable transferable = dragEvent.getTransferable();
      DataFlavor[] flavors = transferable.getTransferDataFlavors();
      for (int i = 0; i < flavors.length; i++) {
        if (transferable.isDataFlavorSupported(flavors[i])) {
          dragEvent.acceptDrop(dragEvent.getDropAction());
          String localFilePath = (String) transferable.getTransferData(flavors[i]);
          File localFile = new File(localFilePath);
          String ftpFullPath = getFtpFullPath(ftpDir, localFile);
          new PutCommand(localFilePath, ftpFullPath, controller).execute();
          dragEvent.dropComplete(true);
          return;
        }
      }
      dragEvent.rejectDrop();
    } catch (Exception e) {
      e.printStackTrace();
      dragEvent.rejectDrop();
    }
  }

  private String getFtpFullPath(FtpTreeFile ftpDir, File localFile) {
    String ftpDirPath = getFtpDirPath(ftpDir);
    if (ftpDirPath.equals("/")) {
      return ftpDirPath + localFile.getName();
    }
    return ftpDirPath + "/" + localFile.getName();
  }

  private String getFtpDirPath(FtpTreeFile dir) {
    if (dir.getDirPath().equals("/")) {
      return dir.getDirPath() + dir.getFileName();
    }
    return dir.getDirPath() + "/" + dir.getFileName();
  }

}
