package pl.edu.agh.kis.clientserverftp.server.commands;

import pl.edu.agh.kis.clientserverftp.server.FtpResponses;

/**
 * FTP QUIT command
 * @author Wojciech Kumoń
 *
 */
public class QuitCommand implements Command {
  private QuitCommand() {}

  /**
   * QUIT command factory method
   * @return new instance of QUIT command
   */
  public static QuitCommand newInstance() {
    return new QuitCommand();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.BYE_221;
  }

}
