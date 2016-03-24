package pl.edu.agh.kis.clientserverftp.client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import pl.edu.agh.kis.clientserverftp.client.view.TextSource;

/**
 * Sends user command to server. If command is recognized as internal protocol, command
 * will be translated into correct commands.
 * @author Wojciech Kumoń
 *
 */
public class SendCommandController implements ActionListener {
  private ClientController controller;
  private TextSource textSource;

  /**
   * Constructs send command controller
   * @param controller client controller
   * @param textSource command source
   */
  public SendCommandController(ClientController controller, TextSource textSource) {
    this.controller = controller;
    this.textSource = textSource;
  }

  /**
   * Sends command.
   * @param e event
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    FtpClientProtocolParser parser = FtpClientProtocolParser.getInstance();
    FtpClientProtocolCommand command = parser.parseCommand(textSource.getText(), controller);
    if (command != null) {
      command.execute();
    } else {
      controller.getMediator().sendAndGetResponse(textSource.getText());
    }
  }

}
