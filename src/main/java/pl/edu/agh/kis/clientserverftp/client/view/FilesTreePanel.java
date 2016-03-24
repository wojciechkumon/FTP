package pl.edu.agh.kis.clientserverftp.client.view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;

import pl.edu.agh.kis.clientserverftp.client.controller.EmptyTreeModel;

/**
 * Panel with JTree
 * @author Wojciech Kumoń
 */
public class FilesTreePanel extends JPanel {
  private static final long serialVersionUID = 1L;
  private JTree filesTree;

  /**
   * Construct panel
   */
  public FilesTreePanel() {
    filesTree = new JTree();
    init();
  }

  /**
   * Sets enabled
   * @param enabled value to set
   */
  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    filesTree.setEnabled(enabled);
  }

  /**
   * Sets tree model
   * @param model tree model to set
   */
  public void setModel(TreeModel model) {
    filesTree.setModel(model);
  }
  
  /**
   * Adds tree mouse listener
   * @param listener listener to add
   */
  public void addTreeMouseListener(MouseListener listener) {
    filesTree.addMouseListener(listener);
  }
  
  /**
   * Returns JTree
   * @return JTree
   */
  public JTree getTree() {
    return filesTree;
  }
  
  private void init() {
    setLayout(new GridLayout());
    Dimension size = new Dimension(400, 400);
    filesTree.setModel(new EmptyTreeModel());
    filesTree.setMaximumSize(size);
    filesTree.setMinimumSize(size);
    filesTree.setSize(size);
    add(filesTree);
    add(new JScrollPane(filesTree));
  }

}
