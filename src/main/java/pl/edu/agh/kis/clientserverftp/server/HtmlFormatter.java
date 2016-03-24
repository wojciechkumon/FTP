package pl.edu.agh.kis.clientserverftp.server;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * HTML formatter for logs.
 * @author Wojciech Kumoń
 *
 */
public class HtmlFormatter extends Formatter {
  private static final HtmlFormatter formatter = new HtmlFormatter();
  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
  private static final DateTimeFormatter timeFormatter =
      DateTimeFormatter.ofPattern("HH:mm:ss:SSS");

  private HtmlFormatter() {}

  /**
   * Returns singleton instance of formatter.
   * @return HtmlFormatter instance
   */
  public static HtmlFormatter getInstance() {
    return formatter;
  }

  @Override
  public String format(LogRecord record) {
    Instant logInstant = Instant.ofEpochMilli(record.getMillis());
    LocalDateTime dateTime = LocalDateTime.ofInstant(logInstant, ZoneId.systemDefault());
    StringBuilder builder = new StringBuilder(150);
    builder.append("<tr>\n");
    builder.append("<td>");
    builder.append(dateTime.format(dateFormatter));
    builder.append("</td>");
    builder.append("<td>");
    builder.append(dateTime.format(timeFormatter));
    builder.append("</td>");
    builder.append("<td>");
    if (record.getMessage() != null) {
      builder.append(record.getMessage());
    }
    builder.append("</td>");
    builder.append("\n</tr>\n");
    return builder.toString();
  }

  @Override
  public String getHead(Handler h) {
    return "<!DOCTYPE html>\n"
        + "<html>\n"
        + "<head>\n"
        + "<meta charset=\"utf-8\">\n"
        + "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n"
        + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
        + "<title>Server logs</title>\n"
        + "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\" "
        + "integrity=\"sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7\" "
        + "crossorigin=\"anonymous\">\n"
        + "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"></script>\n"
        + "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js\" "
        + "integrity=\"sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS\" "
        + "crossorigin=\"anonymous\"></script>\n"
        + "</head>\n"
        + "<body>\n"
        + "<div class=\"container\">\n"
        + "<table class=\"table table-striped\">\n"
        + "<thead>\n"
        + "<tr>\n"
        + "<th>Date</th>\n"
        + "<th>Time</th>\n"
        + "<th>Log</th>\n"
        + "</tr>\n"
        + "</thead>\n"
        + "<tbody>\n";
  }
  
  @Override
  public String getTail(Handler h) {
    return "</tbody>\n</table>\n</div>\n</body>\n</html>";
  }

}
