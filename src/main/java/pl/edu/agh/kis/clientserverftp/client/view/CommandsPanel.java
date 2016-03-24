package pl.edu.agh.kis.clientserverftp.client.view;

import javax.swing.Box;
import javax.swing.JPanel;

import pl.edu.agh.kis.clientserverftp.client.controller.ClientController;

/**
 * Commands panel
 * @author Wojciech Kumoń
 *
 */
public class CommandsPanel extends JPanel {
  private static final long serialVersionUID = -6758518103923384126L;
  private InputCommandPanel inputPanel;
  private InfoPanel infoPanel;

  /**
   * Constructs panel.
   */
  public CommandsPanel() {
    init();
  }
  
  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    inputPanel.setEnabled(enabled);
  }

  /**
   * Adds message
   * @param message message to adde
   * @param type message type
   */
  public void addMessage(String message, MessageType type) {
    infoPanel.addMessage(message, type);
  }
  
  /**
   * Sets controller
   * @param controller client controller
   */
  public void setController(ClientController controller) {
    inputPanel.setController(controller);
  }
  
  private void init() {
    inputPanel = new InputCommandPanel();
    infoPanel = new InfoPanel();

    add(inputPanel);
    add(Box.createHorizontalStrut(30));
    add(infoPanel);
  }

}
