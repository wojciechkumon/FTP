package pl.edu.agh.kis.clientserverftp.server.commands;

import pl.edu.agh.kis.clientserverftp.server.FtpResponses;

/**
 * FTP NOOP command
 * @author Wojciech Kumoń
 *
 */
public class NoopCommand implements Command {
  private NoopCommand() {}

  /**
   * NOOP command factory method
   * @return new instance of NOOP command
   */
  public static NoopCommand newInstance() {
    return new NoopCommand();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return FtpResponses.COMMAND_SUCCESFUL_200;
  }

}
