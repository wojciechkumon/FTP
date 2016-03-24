package pl.edu.agh.kis.clientserverftp.server.commands;

import java.util.regex.Pattern;
/**
 * Server commands patterns.
 * @author Wojciech Kumoń
 *
 */
public class CommandPatterns {
  public static final Pattern USER_PATTERN = Pattern.compile("USER (\\w+)");
  public static final Pattern PASS_PATTERN = Pattern.compile("PASS ?(\\w+)?");

  public static final Pattern EMPTY_LINE = Pattern.compile("^\\s*$");
  public static final Pattern QUIT_PATTERN = Pattern.compile("QUIT");
  public static final Pattern NOOP_PATTERN = Pattern.compile("NOOP");
  public static final Pattern PASV_PATTERN = Pattern.compile("PASV");
  public static final Pattern PORT_PATTERN = Pattern.compile("PORT (\\S+)");
  public static final Pattern STOR_PATTERN = Pattern.compile("STOR (\\S+)");
  public static final Pattern APPE_PATTERN = Pattern.compile("APPE (\\S+)");
  public static final Pattern RETR_PATTERN = Pattern.compile("RETR (\\S+)");
  public static final Pattern ABOR_PATTERN = Pattern.compile("ABOR");
  public static final Pattern DELE_PATTERN = Pattern.compile("DELE (\\S+)");
  public static final Pattern RMD_PATTERN = Pattern.compile("RMD (\\S+)");
  public static final Pattern PWD_PATTERN = Pattern.compile("PWD");
  public static final Pattern MKD_PATTERN = Pattern.compile("MKD (\\S+)");
  public static final Pattern LIST_PATTERN = Pattern.compile("LIST ?(-(\\w)*)?");
  public static final Pattern CWD_PATTERN = Pattern.compile("CWD (\\S+)");
  public static final Pattern CHMOD_PATTERN = Pattern.compile("CHMOD (\\S+) ([0-3])([0-3])");
  public static final Pattern TYPE_PATTERN = Pattern.compile("TYPE (\\S+)");

  private CommandPatterns() {}
}
