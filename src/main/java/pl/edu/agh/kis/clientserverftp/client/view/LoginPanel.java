package pl.edu.agh.kis.clientserverftp.client.view;

import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import pl.edu.agh.kis.clientserverftp.client.controller.LoginDataProvider;

/**
 * Pnael to log in.
 * @author Wojciech Kumoń
 *
 */
public class LoginPanel extends JPanel implements LoginDataProvider {
  private static final long serialVersionUID = 998784368922055062L;

  private JTextField hostFld;
  private JTextField usernameFld;
  private JPasswordField passwordFld;
  private JTextField portFld;
  
  private JButton loginBtn;
  
  /**
   * Construct panel.
   */
  public LoginPanel() {
    init();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getHost() {
    return hostFld.getText();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getUsername() {
    return usernameFld.getText();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public char[] getPassword() {
    return passwordFld.getPassword();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getPort() {
    return portFld.getText();
  }

  /**
   * Sets login controller
   * @param loginController listener which handle login
   */
  public void setLoginController(ActionListener loginController) {
    loginBtn.addActionListener(loginController);
  }
  
  private void init() {
    hostFld = new JTextField(10);
    usernameFld = new JTextField(8);
    passwordFld = new JPasswordField(8);
    portFld = new JTextField("21", 4);
    loginBtn = new JButton("login");
    
    add(new JLabel("host:"));
    add(hostFld);
    add(Box.createHorizontalStrut(20));
    
    add(new JLabel("username:"));
    add(usernameFld);
    add(Box.createHorizontalStrut(20));
    
    add(new JLabel("password:"));
    add(passwordFld);
    add(Box.createHorizontalStrut(20));
    
    add(new JLabel("port:"));
    add(portFld);
    add(Box.createHorizontalStrut(20));
    
    add(loginBtn);
  }

}
