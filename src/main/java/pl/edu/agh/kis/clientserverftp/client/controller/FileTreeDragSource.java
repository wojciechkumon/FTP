package pl.edu.agh.kis.clientserverftp.client.controller;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * Controlls drag from local files tree.
 * @author Wojciech Kumoń
 *
 */
public class FileTreeDragSource implements DragSourceListener, DragGestureListener {
  private DragSource source;
  private JTree sourceTree;

  /**
   * Construct drag source.
   * @param tree tree to control
   * @param actions DnD actions
   */
  public FileTreeDragSource(JTree tree, int actions) {
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
    File file = (File) path.getLastPathComponent();
    TransferablePath transferable = new TransferablePath(file.getAbsolutePath());
    source.startDrag(dge, DragSource.DefaultCopyDrop, transferable, this);
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
