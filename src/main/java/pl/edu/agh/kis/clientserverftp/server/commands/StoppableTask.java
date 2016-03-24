package pl.edu.agh.kis.clientserverftp.server.commands;

/**
 * Interface of stoppable task
 * @author Wojciech Kumoń
 *
 */
public interface StoppableTask extends Runnable {
  /**
   * Stops task.
   */
  void stop();
}
