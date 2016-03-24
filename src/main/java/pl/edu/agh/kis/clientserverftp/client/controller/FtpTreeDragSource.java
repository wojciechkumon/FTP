package pl.edu.agh.kis.clientserverftp.client.controller;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * Controls drag from ftp files tree.
 * @author Wojciech Kumoń
 *
 */
public class FtpTreeDragSource implements DragSourceListener, DragGestureListener {
  private DragSource source;
  private JTree sourceTree;

  /**
   * Constructs drag source
   * @param tree tree to control
   * @param actions DnD actions
   */
  public FtpTreeDragSource(JTree tree, int actions) {
    sourceTree = tree;
    source = new DragSource();
    source.createDefaultDragGestureRecognizer(sourceTree, actions, this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dragGestureRecognized(DragGestureEvent dge) {
    TreePath path = sourceTree.getSelectionPath();
    if ((path == null) || (path.getPathCount() <= 1)) {
      return;
    }
    FtpTreeFile ftpFileNode = (FtpTreeFile) path.getLastPathComponent();
    TransferablePath transferable = new TransferablePath(getFtpFilePath(ftpFileNode));
    source.startDrag(dge, DragSource.DefaultCopyDrop, transferable, this);
  }
  
  private String getFtpFilePath(FtpTreeFile node) {
    if (node.getDirPath().equals("/")) {
      return node.getDirPath() + node.getFileName();
    }
    return node.getDirPath() + "/" + node.getFileName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dragEnter(DragSourceDragEvent dsde) {}

  /**
   * {@inheritDoc}
   */
  @Override
  public void dragExit(DragSourceEvent dse) {}

  /**
   * {@inheritDoc}
   */
  @Override
  public void dragOver(DragSourceDragEvent dsde) {}

  /**
   * {@inheritDoc}
   */
  @Override
  public void dropActionChanged(DragSourceDragEvent dsde) {}

  /**
   * {@inheritDoc}
   */
  @Override
  public void dragDropEnd(DragSourceDropEvent dsde) {}

}
