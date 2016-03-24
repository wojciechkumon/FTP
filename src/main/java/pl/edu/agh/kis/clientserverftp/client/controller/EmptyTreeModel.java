package pl.edu.agh.kis.clientserverftp.client.controller;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Empty tree model.
 * @author Wojciech Kumoń
 *
 */
public class EmptyTreeModel implements TreeModel {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getRoot() {
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getChild(Object parent, int index) {
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getChildCount(Object parent) {
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isLeaf(Object node) {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void valueForPathChanged(TreePath path, Object newValue) {}

  /**
   * {@inheritDoc}
   */
  @Override
  public int getIndexOfChild(Object parent, Object child) {
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addTreeModelListener(TreeModelListener l) {}

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeTreeModelListener(TreeModelListener l) {}

}
