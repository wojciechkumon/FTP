package pl.edu.agh.kis.clientserverftp.client.view;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pl.edu.agh.kis.clientserverftp.client.controller.ClientController;

/**
 * Panel with user command input.
 * @author Wojciech Kumoń
 *
 */
public class InputCommandPanel extends JPanel {
  private static final long serialVersionUID = 4311890512698848117L;
  private JTextField inputField;
  private JButton sendBtn;

  /**
   * Constructs panel.
   */
  public InputCommandPanel() {
    init();
  }

  /**
   * Sets enabled
   * @param enabled value to set
   */
  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    inputField.setEnabled(enabled);
  }

  /**
   * Sets controller
   * @param controller client controller to set
   */
  public void setController(ClientController controller) {
    sendBtn.addActionListener(controller.getSendCommandController(() -> inputField.getText()));
  }

  private void init() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    inputField = new JTextField(32);
    sendBtn = new JButton("send");

    add(new JLabel("Command:"));
    add(inputField);
    add(sendBtn);
  }

}
