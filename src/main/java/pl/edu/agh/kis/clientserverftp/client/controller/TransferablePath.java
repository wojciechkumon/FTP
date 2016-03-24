package pl.edu.agh.kis.clientserverftp.client.controller;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Transferable path, hold file path as a string.
 * @author Wojciech Kumoń
 *
 */
public class TransferablePath implements Transferable {
  public static DataFlavor TREE_PATH_FLAVOR = new DataFlavor(String.class, "File Path");
  private DataFlavor flavors[] = {TREE_PATH_FLAVOR};
  private String path;

  /**
   * Constructs TransferablePath
   * @param path file path
   */
  public TransferablePath(String path) {
    this.path = path;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized DataFlavor[] getTransferDataFlavors() {
    return flavors;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return (flavor.getRepresentationClass() == String.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized String getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException, IOException {
    if (isDataFlavorSupported(flavor)) {
      return (String) path;
    } else {
      throw new UnsupportedFlavorException(flavor);
    }
  }

}
