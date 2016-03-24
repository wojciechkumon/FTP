package pl.edu.agh.kis.clientserverftp.client.view;

import java.awt.BorderLayout;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.tree.TreeModel;

import pl.edu.agh.kis.clientserverftp.client.controller.ClientController;

/**
 * Client main panel.
 * @author Wojciech Kumoń
 *
 */
public class MainPanel extends JPanel {
  private static final long serialVersionUID = -803469041935055591L;
  private CommandsPanel commandsPanel;
  private FileViewersPanel fileViewersPanel;

  /**
   * Constructs panel.
   */
  public MainPanel() {
    init();
  }

  /**
   * Sets enabled
   * @param enabled value to set
   */
  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    commandsPanel.setEnabled(enabled);
    fileViewersPanel.setEnabled(enabled);
  }

  /**
   * Adds message.
   * @param message message to add
   * @param type message type
   */
  public void addMessage(String message, MessageType type) {
    commandsPanel.addMessage(message, type);
  }

  /**
   * Sets server files tree model
   * @param model model to set
   */
  public void setServerFilesTreeModel(TreeModel model) {
    fileViewersPanel.setServerFilesTreeModel(model);
  }
  
  /**
   * Adds server tree mouse listener
   * @param listener listener to add
   */
  public void addServerTreeMouseListener(MouseListener listener) {
    fileViewersPanel.addServerTreeMouseListener(listener);
  }
  
  /**
   * Sets client controller.
   * @param controller client controller to set
   */
  public void setController(ClientController controller) {
    fileViewersPanel.setController(controller);
    commandsPanel.setController(controller);
  }

  private void init() {
    setLayout(new BorderLayout());
    commandsPanel = new CommandsPanel();
    fileViewersPanel = new FileViewersPanel();

    add(commandsPanel, BorderLayout.NORTH);
    add(fileViewersPanel, BorderLayout.CENTER);
  }

}
