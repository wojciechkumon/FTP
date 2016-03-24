package pl.edu.agh.kis.clientserverftp.client.controller;

import java.io.Serializable;

/**
 * Represents ftp file (node) in ftp file tree.
 * @author Wojciech Kumoń
 *
 */
public class FtpTreeFile implements Serializable {
  private static final long serialVersionUID = -2846908196180567713L;
  private String dirPath;
  private String fileName;
  private boolean isFile;
  private int index = -1;

  /**
   * Constructs ftp tree file with default node index=-1
   * @param dirPath parent directory path
   * @param fileName file name
   * @param isFile true if is file, false otherwise
   */
  public FtpTreeFile(String dirPath, String fileName, boolean isFile) {
    this.dirPath = dirPath;
    this.fileName = fileName;
    this.isFile = isFile;
  }

  /**
   * Returns directory path
   * @return directory path
   */
  public String getDirPath() {
    return dirPath;
  }

  /**
   * Sets directory path
   * @param dirPath directory path to set
   */
  public void setDirPath(String dirPath) {
    this.dirPath = dirPath;
  }

  /**
   * Returns file name
   * @return file name
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Sets file name
   * @param fileName file name to set
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * Checks if this is file or directory
   * @return true if this is file, false otherwise
   */
  public boolean isFile() {
    return isFile;
  }

  /**
   * Sets file status
   * @param isFile file status to set
   */
  public void setFile(boolean isFile) {
    this.isFile = isFile;
  }

  /**
   * Returns node index
   * @return node index
   */
  public int getIndex() {
    return index;
  }

  /**
   * Sets node index
   * @param index node index to set
   */
  public void setIndex(int index) {
    this.index = index;
  }

  /**
   * Returns file name
   * @return file name
   */
  @Override
  public String toString() {
    return fileName;
  }

}
