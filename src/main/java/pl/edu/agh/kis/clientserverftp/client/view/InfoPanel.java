package pl.edu.agh.kis.clientserverftp.client.view;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Panel with informations.
 * @author Wojciech Kumoń
 *
 */
public class InfoPanel extends JPanel {
  private static final long serialVersionUID = 8769702514452549315L;
  private static final Color inputColor = new Color(0, 102, 0);
  private JTextPane infoPane;

  /**
   * Contructs panel.
   */
  public InfoPanel() {
    init();
  }

  /**
   * Adds message
   * @param message message to add
   * @param type type of message to add
   */
  public void addMessage(String message, MessageType type) {
    switch (type) {
      case INFO:
        appendToPane("\nINFO: " + message, Color.DARK_GRAY);
        break;
      case RESPONSE:
        appendToPane("\nRESPONSE: " + message, Color.BLUE);
        break;
      case INPUT:
      default:
        appendToPane("\nINPUT: " + message, inputColor);
    }
  }

  private void appendToPane(String msg, Color color) {
    StyledDocument doc = infoPane.getStyledDocument();

    SimpleAttributeSet attrSet = new SimpleAttributeSet();
    StyleConstants.setForeground(attrSet, color);
    try {
      doc.insertString(doc.getLength(), msg, attrSet);
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
  }

  private void init() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    infoPane = new JTextPane();
    Dimension dim = new Dimension(390, 70);
    infoPane.setSize(dim);
    infoPane.setPreferredSize(dim);
    infoPane.setMinimumSize(dim);
    infoPane.setEditable(false);

    add(new JLabel("Server responses:"));
    add(new JScrollPane(infoPane));
  }

}
