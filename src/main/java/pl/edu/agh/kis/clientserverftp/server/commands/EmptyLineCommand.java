package pl.edu.agh.kis.clientserverftp.server.commands;

import pl.edu.agh.kis.clientserverftp.server.FtpResponses;

/**
 * FTP empty line command
 * @author Wojciech Kumoń
 *
 */
public class EmptyLineCommand implements Command {
  private EmptyLineCommand() {}

  /**
   * Empty line command factory method
   * @return new instance of empty line command
   */
  public static EmptyLineCommand newInstance() {
    return new EmptyLineCommand();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.EMPTY_LINE_500;
  }

}
