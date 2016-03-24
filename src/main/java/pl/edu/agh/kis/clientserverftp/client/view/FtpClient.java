package pl.edu.agh.kis.clientserverftp.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.tree.TreeModel;

import pl.edu.agh.kis.clientserverftp.client.controller.ClientController;
import pl.edu.agh.kis.clientserverftp.client.controller.LoginDataProvider;

/**
 * Ftp Client main window vlass
 * @author Wojciech Kumoń
 *
 */
public class FtpClient extends JFrame {
  private static final long serialVersionUID = -6349749478901574246L;
  private LoginPanel loginPanel;
  private MainPanel mainPanel;
  
  /**
   * Constructs window
   */
  public FtpClient() {
    init();
    setVisible(true);
  }

  /**
   * Returns login data provider
   * @return login data provider
   */
  public LoginDataProvider getLoginDataProvider() {
    return loginPanel;
  }

  /**
   * Sets login controller
   * @param loginController actionListener which handles login
   */
  public void setLoginController(ActionListener loginController) {
    loginPanel.setLoginController(loginController);
  }
  
  /**
   * Adds message
   * @param message message to add
   * @param type type of message to add
   */
  public void addMessage(String message, MessageType type) {
    mainPanel.addMessage(message, type);
  }
  
  /**
   * Sets server files tree model
   * @param model tree model to set
   */
  public void setServerFilesTreeModel(TreeModel model) {
    mainPanel.setServerFilesTreeModel(model);
  }
  
  /**
   * Adds server tree mouse listener
   * @param listener mouse listener to add
   */
  public void addServerTreeMouseListener(MouseListener listener) {
    mainPanel.addServerTreeMouseListener(listener);
  }
  
  /**
   * Sets client controller
   * @param controller client controller to set
   */
  public void setController(ClientController controller) {
    mainPanel.setController(controller);
  }

  /**
   * Sets enabled
   * @param enabled value to set
   */
  @Override
  public void setEnabled(boolean enabled) {
    mainPanel.setEnabled(enabled);
  }
  
  private void init() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout(10, 20));
    setTitle("Ftp Client by wojtas626");
    int windowWidth = 882;
    int windowHeight = 675;
    int minWindowHeight = 400;
    Dimension windowDimension = new Dimension(windowWidth, windowHeight);
    setSize(windowDimension);
    setMinimumSize(new Dimension(windowWidth, minWindowHeight));
    setForeground(new Color(51, 51, 51));
    
    loginPanel = new LoginPanel();
    mainPanel = new MainPanel();
    mainPanel.setEnabled(false);
    
    add(loginPanel, BorderLayout.PAGE_START);
    add(mainPanel, BorderLayout.CENTER);
    add(new JPanel(), BorderLayout.EAST);
    add(new JPanel(), BorderLayout.WEST);
    add(new JPanel(), BorderLayout.SOUTH);
    
    int width = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
        .getDisplayMode().getWidth();
    int height = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
        .getDisplayMode().getHeight();
    setLocation((width - getWidth()) / 2, (height - getHeight()) / 2);
  }
  
}
