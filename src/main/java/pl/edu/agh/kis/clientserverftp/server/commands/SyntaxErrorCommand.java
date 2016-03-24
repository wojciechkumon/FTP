package pl.edu.agh.kis.clientserverftp.server.commands;

import pl.edu.agh.kis.clientserverftp.server.FtpResponses;

/**
 * FTP syntax error command
 * @author Wojciech Kumoń
 *
 */
public class SyntaxErrorCommand implements Command {
  private String firstWord;

  private SyntaxErrorCommand(String command) {
    firstWord = command.split(" ")[0];
  }
  
  /**
   * Syntax error command factory method
   * @param command full command
   * @return new instance of syntax error command
   */
  public static SyntaxErrorCommand newInstance(String command) {
    return new SyntaxErrorCommand(command);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String execute() {
    return String.format(FtpResponses.SYNTAX_ERROR_500, firstWord);
  }

}
