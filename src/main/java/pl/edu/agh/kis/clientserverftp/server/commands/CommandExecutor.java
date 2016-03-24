package pl.edu.agh.kis.clientserverftp.server.commands;

import static pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns.*;
import static pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns.APPE_PATTERN;
import static pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns.CHMOD_PATTERN;
import static pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns.CWD_PATTERN;
import static pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns.DELE_PATTERN;
import static pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns.EMPTY_LINE;
import static pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns.LIST_PATTERN;
import static pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns.MKD_PATTERN;
import static pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns.NOOP_PATTERN;
import static pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns.PASS_PATTERN;
import static pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns.PASV_PATTERN;
import static pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns.PORT_PATTERN;
import static pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns.PWD_PATTERN;
import static pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns.QUIT_PATTERN;
import static pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns.RETR_PATTERN;
import static pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns.RMD_PATTERN;
import static pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns.STOR_PATTERN;
import static pl.edu.agh.kis.clientserverftp.server.commands.CommandPatterns.USER_PATTERN;

import java.util.regex.Pattern;

import pl.edu.agh.kis.clientserverftp.server.ClientStatus;
import pl.edu.agh.kis.clientserverftp.server.DataSocket;
import pl.edu.agh.kis.clientserverftp.server.database.Database;

/**
 * Executes commands.
 * @author Wojciech Kumoń
 *
 */
public class CommandExecutor {
  private ClientStatus status;
  private DataSocket dataSocket;

  /**
   * Construct executor
   * @param status client status
   * @param dataSocket data socket
   */
  public CommandExecutor(ClientStatus status, DataSocket dataSocket) {
    this.status = status;
    this.dataSocket = dataSocket;
  }

  /**
   * Executes command basing on string
   * @param command command to execute
   * @return Executed command response
   */
  public String executeCommand(String command) {
    Command cmd = getCommand(command);
    status.setLastCommand(command);
    return cmd.execute();
  }

  private Command getCommand(String command) {
    if (isCommandMatching(QUIT_PATTERN, command)) {
      return QuitCommand.newInstance();
    } else if (isCommandMatching(NOOP_PATTERN, command)) {
      return NoopCommand.newInstance();
    } else if (isCommandMatching(PASV_PATTERN, command)) {
      return PasvCommand.newInstance(status, dataSocket);
    } else if (isCommandMatching(PORT_PATTERN, command)) {
      return PortCommand.newInstance(status.getLoginStatus(), command, dataSocket);
    } else if (isCommandMatching(STOR_PATTERN, command)) {
      return StorCommand.newInstance(status, command, dataSocket, Database.getInstance());
    } else if (isCommandMatching(APPE_PATTERN, command)) {
      return AppeCommand.newInstance(status, command, dataSocket, Database.getInstance());
    } else if (isCommandMatching(RETR_PATTERN, command)) {
      return RetrCommand.newInstance(status, command, dataSocket, Database.getInstance());
    } else if (isCommandMatching(ABOR_PATTERN, command)) {
      return AborCommand.newInstance(status.getLoginStatus(), dataSocket);
    } else if (isCommandMatching(DELE_PATTERN, command)) {
      return DeleCommand.newInstance(status, command, Database.getInstance());
    } else if (isCommandMatching(RMD_PATTERN, command)) {
      return RmdCommand.newInstance(status, command, Database.getInstance());
    } else if (isCommandMatching(MKD_PATTERN, command)) {
      return MkdCommand.newInstance(status, command, Database.getInstance());
    } else if (isCommandMatching(PWD_PATTERN, command)) {
      return PwdCommand.newInstance(status);
    } else if (isCommandMatching(LIST_PATTERN, command)) {
      return ListCommand.newInstance(status, dataSocket);
    } else if (isCommandMatching(CWD_PATTERN, command)) {
      return CwdCommand.newInstance(status, command, Database.getInstance());
    } else if (isCommandMatching(CHMOD_PATTERN, command)) {
      return ChmodCommand.newInstance(status, command, Database.getInstance());
    } else if (isCommandMatching(USER_PATTERN, command)) {
      return UserCommand.newInstance(status.getLoginStatus(), command);
    } else if (isCommandMatching(PASS_PATTERN, command)) {
      return PassCommand.newInstance(status, command, Database.getInstance());
    } else if (isCommandMatching(TYPE_PATTERN, command)) {
      return TypeCommand.newInstance();
    } else if (isCommandMatching(EMPTY_LINE, command)) {
      return EmptyLineCommand.newInstance();
    }
    return SyntaxErrorCommand.newInstance(command);
  }

  private boolean isCommandMatching(Pattern pattern, String command) {
    return pattern.matcher(command).matches();
  }

}
