package pl.edu.agh.kis.clientserverftp.server;

/**
 * All server response codes.
 * 
 * @author Wojciech Kumoń
 */
public class FtpResponses {
  public static final String FILE_150 = "150 FILE: '%s'";
  public static final String OPENING_BINARY_CONNECTION_150 =
      "150 Opening binary mode data connection for '%s'";
  public static final String ASCII_CONNECTION_LS_150 =
      "150 Opening ASCII mode data connection for file list";

  public static final String COMMAND_SUCCESFUL_200 = "200 Command succesful";
  public static final String WELCOME_220 = "220 Welcome to FTP server by wojtas626";
  public static final String BYE_221 = "221 Bye";
  public static final String TRANSFER_COMPLETE_226 = "226 Transfer complete";
  public static final String ABORT_SUCCESSFUL_226 = "226 Abort successful";
  public static final String ENTERING_PASSIVE_MODE_227 = "227 Entering Passive Mode (%s)";
  public static final String USER_LOGGED_IN_230 = "230 User %s logged in";
  public static final String DELE_SUCCESFUL_250 = "250 DELE was successful";
  public static final String RMD_SUCCESFUL_250 = "250 RMD was successful";
  public static final String CWD_SUCCESFUL_250 = "250 CWD was successful";
  public static final String CHMOD_SUCCESFUL_250 = "250 CHMOD was successful";
  public static final String DIR_CREATED_257 = "257 pathname was created";
  public static final String CURRENT_DIR_257 = "257 \"%s\" is current directory";

  public static final String PASSWORD_REQUIRED_331 = "331 Password required for %s";

  public static final String CANT_OPEN_DATA_CONNECTION_425 = "425 Can't open data connection";
  public static final String CANT_OPEN_PASSIVE_PORT_425 = "425 Can't open passive data port";
  public static final String USE_PORT_OR_PASV_425 = "425 Use PORT or PASV first";
  public static final String WRONG_PASSWORD_430 = "430 Invalid username or password";
  public static final String HOST_NOT_AVAILABLE_434 = "434 Requested host unavailable";

  public static final String SYNTAX_ERROR_500 = "500 %s not understood";
  public static final String EMPTY_LINE_500 = "500 Invalid command: try being more creative";
  public static final String ARGUMENT_SYNTAX_ERROR_501 =
      "501 Syntax error in parameters or arguments";
  public static final String BAD_SEQUENCE_503 = "503 Bad sequence of commands";
  public static final String YOU_ARE_LOGGED_IN_503 = "503 You are already logged in";
  public static final String NO_SUCH_FILE_OR_DIRECTORY_550 = "550 No such file or directory";
  public static final String DIRECTORY_NOT_EMPTY_550 = "550 %s: Directory not empty";
  public static final String FILE_IS_A_DIRECTORY_550 = "550 %s: Is a directory";
  public static final String NOT_A_DIRECTORY_550 = "550 %s: Not a directory";
  public static final String FILE_EXIST_550 = "550 %s: File exists";
  public static final String PERMISSION_DENIED_550 = "550 Permission denied";

  private FtpResponses() {}
}
