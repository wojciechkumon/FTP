package pl.edu.agh.kis.clientserverftp.server.commands;

import pl.edu.agh.kis.clientserverftp.server.FtpResponses;

/**
 * FTP argument syntax error command
 * @author Wojciech Kumoń
 *
 */
public class ArgumentSyntaxErrorCommand implements Command {

  private ArgumentSyntaxErrorCommand() {}

  /**
   * Argument syntax error command factory method
   * @return new instance of ArgumentSyntaxErrorCommand
   */
  public static ArgumentSyntaxErrorCommand newInstance() {
    return new ArgumentSyntaxErrorCommand();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.ARGUMENT_SYNTAX_ERROR_501;
  }

}
