package pl.edu.agh.kis.clientserverftp.server.commands;

/**
 * Command interface
 * 
 * @author Wojciech Kumoń
 *
 */
public interface Command {
  
  /**
   * Executes command.
   * 
   * @return Response message.
   */
  String execute();
}
