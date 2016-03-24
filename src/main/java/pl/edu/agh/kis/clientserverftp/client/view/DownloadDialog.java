package pl.edu.agh.kis.clientserverftp.client.view;

import java.awt.FlowLayout;
import java.awt.Point;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import pl.edu.agh.kis.clientserverftp.client.controller.ClientController;

/**
 * Download window which allows aborting.
 * 
 * @author Wojciech Kumoń
 */
public class DownloadDialog extends JDialog {
  private static final long serialVersionUID = 7159889146151454094L;
  private Future<Boolean> future;
  private Path pathToSave;

  /**
   * Dialog constructor without deleting file after abort
   * 
   * @param controller client controller
   * @param title dialog title
   * @param future download/upload future
   */
  public DownloadDialog(ClientController controller, String title, Future<Boolean> future) {
    this(controller, title, future, null);
  }

  /**
   * Dialog constructor which will delete downloaded file after abort.
   * 
   * @param controller client controller
   * @param title dialog title
   * @param future download/upload future
   * @param pathToSave
   */
  public DownloadDialog(ClientController controller, String title, Future<Boolean> future,
      Path pathToSave) {
    super(controller.getView(), title);
    this.future = future;
    this.pathToSave = pathToSave;
    setLayout(new FlowLayout());
    int width = 200;
    int height = 100;
    setSize(width, height);
    JFrame owner = controller.getView();
    Point ownerLocation = owner.getLocation();
    Point dialogLocation = new Point(ownerLocation.x + ((owner.getWidth() - width) / 2),
        ownerLocation.y + ((owner.getHeight() - height) / 2));
    setLocation(dialogLocation);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    JButton button = new JButton("ABORT");
    button.addActionListener(l -> {
      controller.getMediator().sendAndGetResponse("ABOR");
      future.cancel(true);
      DownloadDialog.this.setVisible(false);
      try {
        if (DownloadDialog.this.pathToSave != null) {
          Files.deleteIfExists(DownloadDialog.this.pathToSave);
        } else {
          controller.clearCache();
        }
      } catch (Exception e) {
        System.out.println(e);
      }
    });
    panel.add(Box.createVerticalStrut(20));
    panel.add(button);
    add(button);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setVisible(true);
    repaint();
  }

  /**
   * Allows closing dialog after download/upload.
   */
  public void waitForTransfer() {
    new Thread(() -> {
      try {
        future.get();
      } catch (InterruptedException | ExecutionException | CancellationException e) {
        // ignore
      } finally {
        SwingUtilities.invokeLater(() -> DownloadDialog.this.setVisible(false));
      }
    }).start();
  }

}
