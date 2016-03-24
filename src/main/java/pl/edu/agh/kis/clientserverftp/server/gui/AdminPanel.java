package pl.edu.agh.kis.clientserverftp.server.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Server admin panel window.
 * @author Wojciech Kumoń
 */
public class AdminPanel extends JFrame {
  private static final long serialVersionUID = -3622554953149537165L;
  private SwitchViewPanel switchViewPanel;
  private TablePanel tablePanel;

  /**
   * Constructs window.
   */
  public AdminPanel() {
    init();
    setVisible(true);
  }
  
  /**
   * Switches viewed table.
   * @param type table type to view
   */
  public void switchTable(TableType type) {
    tablePanel.setVisible(false);
    remove(tablePanel);
    tablePanel = TablePanelFactory.create(type);
    add(tablePanel, BorderLayout.CENTER);
  }

  /**
   * Sets controller.
   * @param serverPanelController controller object
   */
  public void setController(ServerPanelController serverPanelController) {
    switchViewPanel.setController(serverPanelController);
  }

  private void init() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout(10, 20));
    setTitle("FTP Server Admin Panel");
    int windowWidth = 600;
    int windowHeight = 575;
    int minWindowHeight = 400;
    Dimension windowDimension = new Dimension(windowWidth, windowHeight);
    setSize(windowDimension);
    setMinimumSize(new Dimension(windowWidth, minWindowHeight));
    setForeground(new Color(51, 51, 51));

    switchViewPanel = new SwitchViewPanel();
    tablePanel = new UsersPanel();

    add(switchViewPanel, BorderLayout.PAGE_START);
    add(tablePanel, BorderLayout.CENTER);
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
