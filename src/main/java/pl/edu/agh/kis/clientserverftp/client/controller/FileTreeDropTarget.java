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
 * Controls drop on local files tree.
 * @author Wojciech Kumoń
 *
 */
public class FileTreeDropTarget implements DropTargetListener {
  private JTree targetTree;
  private ClientController controller;

  /**
   * Constructs drop targer
   * @param tree tree to control
   * @param controller client controller
   */
  public FileTreeDropTarget(JTree tree, ClientController controller) {
    targetTree = tree;
    this.controller = controller;
    new DropTarget(targetTree, this);
  }

  private File getNodeForEvent(DropTargetDragEvent dragEvent) {
    Point point = dragEvent.getLocation();
    DropTargetContext targetContext = dragEvent.getDropTargetContext();
    JTree tree = (JTree) targetContext.getComponent();
    TreePath path = tree.getClosestPathForLocation(point.x, point.y);
    return (File) path.getLastPathComponent();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dragEnter(DropTargetDragEvent dragEvent) {
    File node = getNodeForEvent(dragEvent);
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
    File node = getNodeForEvent(dragEvent);
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
    File parent = (File) parentpath.getLastPathComponent();
    if (parent.isFile()) {
      dragEvent.rejectDrop();
      return;
    }

    try {
      Transferable transferable = dragEvent.getTransferable();
      DataFlavor[] flavors = transferable.getTransferDataFlavors();
      for (int i = 0; i < flavors.length; i++) {
        if (transferable.isDataFlavorSupported(flavors[i])) {
          dragEvent.acceptDrop(dragEvent.getDropAction());
          String ftpPath = (String) transferable.getTransferData(flavors[i]);
          new GetCommand(ftpPath, parent.getAbsolutePath(), controller).execute();
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

}
