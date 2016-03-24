package pl.edu.agh.kis.clientserverftp.server.commands;

import pl.edu.agh.kis.clientserverftp.server.FtpResponses;

/**
 * FTP TYPE command
 * @author Wojciech Kumoń
 *
 */
public class TypeCommand implements Command {
  private TypeCommand() {}

  /**
   * TYPE command factory method
   * @return new instance of TYPE command
   */
  public static TypeCommand newInstance() {
    return new TypeCommand();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.COMMAND_SUCCESFUL_200;
  }

}
