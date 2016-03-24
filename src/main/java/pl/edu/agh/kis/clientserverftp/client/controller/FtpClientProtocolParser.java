package pl.edu.agh.kis.clientserverftp.client.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ftp client commands parser.
 * @author Wojciech Kumoń
 *
 */
public class FtpClientProtocolParser {
  private static final Pattern GET_PATTERN = Pattern.compile("[gG][eE][tT] (\\S+) (\\S+)");
  private static final Pattern PUT_PATTERN = Pattern.compile("[pP][uU][tT] (\\S+) (\\S+)");
  private static final FtpClientProtocolParser instance = new FtpClientProtocolParser();

  private FtpClientProtocolParser() {}

  /**
   * Returns parser instance
   * @return parser instance
   */
  public static FtpClientProtocolParser getInstance() {
    return instance;
  }

  /**
   * Returns correct command from string
   * @param command command
   * @param controller client controller
   * @return correct FtpClientProtocolCommand or null if syntax is wrong
   */
  public FtpClientProtocolCommand parseCommand(String command, ClientController controller) {
    Matcher matcher;
    if ((matcher = GET_PATTERN.matcher(command)).matches()) {
      return new GetCommand(matcher.group(1), matcher.group(2), controller);
    } else if ((matcher = PUT_PATTERN.matcher(command)).matches()) {
      return new PutCommand(matcher.group(1), matcher.group(2), controller);
    }
    return null;
  }

}
