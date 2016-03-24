package pl.edu.agh.kis.clientserverftp.client.view;

import java.awt.GridLayout;
import java.awt.dnd.DnDConstants;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.tree.TreeModel;

import pl.edu.agh.kis.clientserverftp.client.controller.ClientController;
import pl.edu.agh.kis.clientserverftp.client.controller.FileTreeDragSource;
import pl.edu.agh.kis.clientserverftp.client.controller.FileTreeDropTarget;
import pl.edu.agh.kis.clientserverftp.client.controller.FtpTreeDragSource;
import pl.edu.agh.kis.clientserverftp.client.controller.FtpTreeDropTarget;

/**
 * File Viewers panel. Contains two file managers.
 * @author Wojciech Kumoń
 *
 */
public class FileViewersPanel extends JPanel {
  private static final long serialVersionUID = 7109313880194121214L;
  private FilesTreePanel localFilesPanel;
  private FilesTreePanel serverFilesPanel;

  /**
   * Constructs panel
   */
  public FileViewersPanel() {
    init();
  }

  /**
   * Sets enabled
   * @param enabled value to set
   */
  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    localFilesPanel.setEnabled(enabled);
    serverFilesPanel.setEnabled(enabled);
  }

  /**
   * Sets server tree model
   * @param model tree model to set
   */
  public void setServerFilesTreeModel(TreeModel model) {
    serverFilesPanel.setModel(model);
  }

  /**
   * Adds server tree mouse listener
   * @param listener mouse listener to add
   */
  public void addServerTreeMouseListener(MouseListener listener) {
    serverFilesPanel.addTreeMouseListener(listener);
  }

  /**
   * Sets controller
   * @param controller client controller
   */
  public void setController(ClientController controller) {
    localFilesPanel.setModel(controller.getLocalFilesModel());

    new FtpTreeDragSource(serverFilesPanel.getTree(), DnDConstants.ACTION_COPY);
    new FileTreeDropTarget(localFilesPanel.getTree(), controller);

    new FileTreeDragSource(localFilesPanel.getTree(), DnDConstants.ACTION_COPY);
    new FtpTreeDropTarget(serverFilesPanel.getTree(), controller);
  }

  private void init() {
    setLayout(new GridLayout(1, 2, 40, 10));

    localFilesPanel = new FilesTreePanel();
    serverFilesPanel = new FilesTreePanel();

    add(localFilesPanel);
    add(serverFilesPanel);
  }

}
