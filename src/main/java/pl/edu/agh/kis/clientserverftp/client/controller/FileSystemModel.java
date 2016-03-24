package pl.edu.agh.kis.clientserverftp.client.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Local file system model
 * @author Wojciech Kumoń
 *
 */
public class FileSystemModel implements TreeModel {
  private File root = new File("/");
  private List<TreeModelListener> listeners = new ArrayList<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public File getRoot() {
    return root;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public File getChild(Object parent, int index) {
    File directory = (File) parent;
    String[] children = directory.list();
    return new TreeFile(directory, children[index]);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getChildCount(Object parent) {
    File file = (File) parent;
    if (file.isDirectory()) {
      String[] fileList = file.list();
      if (fileList != null)
        return fileList.length;
    }
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isLeaf(Object node) {
    File file = (File) node;
    return file.isFile();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getIndexOfChild(Object parent, Object child) {
    File directory = (File) parent;
    File file = (File) child;
    String[] children = directory.list();
    for (int i = 0; i < children.length; i++) {
      if (file.getName().equals(children[i])) {
        return i;
      }
    }
    return -1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void valueForPathChanged(TreePath path, Object value) {
    File oldFile = (File) path.getLastPathComponent();
    String fileParentPath = oldFile.getParent();
    String newFileName = (String) value;
    File targetFile = new File(fileParentPath, newFileName);
    oldFile.renameTo(targetFile);
    File parent = new File(fileParentPath);
    int[] changedChildrenIndices = {getIndexOfChild(parent, targetFile)};
    Object[] changedChildren = {targetFile};
    fireTreeNodesChanged(path.getParentPath(), changedChildrenIndices, changedChildren);
  }

  private void fireTreeNodesChanged(TreePath parentPath, int[] indices, Object[] children) {
    TreeModelEvent event = new TreeModelEvent(this, parentPath, indices, children);
    for (TreeModelListener listener : listeners) {
      listener.treeNodesChanged(event);
    }
  }

  /**
   * Refresh model.
   */
  public void refresh() {
    for (TreeModelListener listener : listeners) {
      listener.treeStructureChanged(new TreeModelEvent(this, new TreePath(root)));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addTreeModelListener(TreeModelListener listener) {
    listeners.add(listener);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeTreeModelListener(TreeModelListener listener) {
    listeners.remove(listener);
  }

  private static class TreeFile extends File {
    private static final long serialVersionUID = 7676705593340935597L;

    public TreeFile(File parent, String child) {
      super(parent, child);
    }

    @Override
    public String toString() {
      return getName();
    }
  }

}
