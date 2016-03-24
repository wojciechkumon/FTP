package pl.edu.agh.kis.clientserverftp.client.controller;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Ftp files system model.
 * 
 * @author Wojciech Kumoń
 *
 */
public class FtpSystemModel implements TreeModel {
  private static Pattern rowRegex = Pattern.compile(
      "(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(.+)\\r?");
  private ClientController controller;
  private ServerMediator mediator;
  private List<TreeModelListener> listeners = new ArrayList<>();
  private LoadingCache<String, String> cachedListOutputs;

  /**
   * Constructs ftp system model.
   * 
   * @param controller client controller
   */
  public FtpSystemModel(ClientController controller) {
    this.controller = controller;
    this.mediator = controller.getMediator();
    cachedListOutputs = CacheBuilder.newBuilder().concurrencyLevel(4).maximumSize(100)
        .expireAfterWrite(10, TimeUnit.MINUTES).build(new CacheLoader<String, String>() {
          public String load(String dirFullPath) {
            String list = getNewListOutput(dirFullPath);
            list = sortDotDirectories(list);
            return list;
          }

          private String sortDotDirectories(String list) {
            String[] rows = list.split("\n");
            for (int i = 1; i < rows.length; i++) {
              if (getFileName(rows[i]).equals(".")) {
                String tmp = rows[0];
                rows[0] = rows[i];
                rows[i] = tmp;
              }
            }
            for (int i = 0; i < rows.length; i++) {
              if (getFileName(rows[i]).equals("..")) {
                String tmp = rows[1];
                rows[1] = rows[i];
                rows[i] = tmp;
              }
            }
            String newListOutput = concatStrings(rows);
            return newListOutput;
          }

          private String concatStrings(String[] rows) {
            StringBuilder builder = new StringBuilder();
            for (String row : rows) {
              builder.append(row + "\n");
            }
            return builder.toString();
          }
        });
  }

  /**
   * Returns specyfied node.
   * 
   * @param dirPath file parent path
   * @param fileName file name
   * @return correct node
   */
  public FtpTreeFile getNode(String dirPath, String fileName) {
    String[] splittedDir = dirPath.split("/");
    FtpTreeFile parent = (FtpTreeFile) getRoot();
    for (int i = 1; i < splittedDir.length; i++) {
      int index = getIndexOfChild(parent, new FtpTreeFile(
          concatPaths(parent.getDirPath(), parent.getFileName()), splittedDir[i], false)) - 1;
      parent = (FtpTreeFile) getChild(parent, index);
    }
    int index = getIndexOfChild(parent,
        new FtpTreeFile(concatPaths(parent.getDirPath(), parent.getFileName()), fileName, false))
        - 1;
    return (FtpTreeFile) getChild(parent, index);
  }

  private String concatPaths(String dirPath, String filePath) {
    return dirPath + "/" + filePath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FtpTreeFile getRoot() {
    return new FtpTreeFile("", "", false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FtpTreeFile getChild(Object parent, int index) {
    FtpTreeFile parentFile = (FtpTreeFile) parent;
    String dirFullPath = createFullPath(parentFile);
    String listOutput = cachedListOutputs.getUnchecked(dirFullPath);
    String[] rows = listOutput.split("\n");
    String correctRow = rows[index + 2];
    String permissions = getPermissions(correctRow);
    boolean isFile = checkIfIsFile(permissions);
    String fileName = getFileName(correctRow);
    return new FtpTreeFile(dirFullPath, fileName, isFile);
  }

  private String getPermissions(String correctRow) {
    Matcher matcher = rowRegex.matcher(correctRow);
    if (matcher.matches()) {
      return matcher.group(1);
    }
    return "";
  }

  private String getFileName(String correctRow) {
    Matcher matcher = rowRegex.matcher(correctRow);
    if (matcher.matches()) {
      return matcher.group(9);
    }
    return "";
  }

  private boolean checkIfIsFile(String permissions) {
    return permissions.isEmpty() || (permissions.charAt(0) == '-');
  }

  private String createFullPath(FtpTreeFile parentFile) {
    if (parentFile.getDirPath().equals("/"))
      return parentFile.getDirPath() + parentFile.getFileName();
    return parentFile.getDirPath() + "/" + parentFile.getFileName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getChildCount(Object parent) {
    FtpTreeFile parentFile = (FtpTreeFile) parent;
    String dirFullPath = createFullPath(parentFile);
    String listOutput = cachedListOutputs.getUnchecked(dirFullPath);
    return listOutput.split("\n").length - 2;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isLeaf(Object node) {
    FtpTreeFile treeFile = (FtpTreeFile) node;
    return treeFile.isFile();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void valueForPathChanged(TreePath path, Object value) {
    FtpTreeFile oldFile = (FtpTreeFile) path.getLastPathComponent();
    String newFileName = (String) value;
    oldFile.setFileName(newFileName);
    String parentPath = Paths.get(oldFile.getDirPath()).resolve("..").normalize().toString();
    String parentFileName = Paths.get(parentPath).resolve(oldFile.getDirPath()).toString();
    FtpTreeFile target = new FtpTreeFile(oldFile.getDirPath(), newFileName, oldFile.isFile());
    FtpTreeFile parent = new FtpTreeFile(parentPath, parentFileName, false);
    int[] changedChildrenIndices = {getIndexOfChild(parent, target)};
    Object[] changedChildren = {target};
    fireTreeNodesChanged(path.getParentPath(), changedChildrenIndices, changedChildren);
  }

  private void fireTreeNodesChanged(TreePath parentPath, int[] indices, Object[] children) {
    TreeModelEvent event = new TreeModelEvent(this, parentPath, indices, children);
    for (TreeModelListener listener : listeners) {
      listener.treeNodesChanged(event);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getIndexOfChild(Object parent, Object child) {
    FtpTreeFile childFile = (FtpTreeFile) child;
    if (childFile.getIndex() >= 0) {
      return childFile.getIndex();
    }
    FtpTreeFile parentFile = (FtpTreeFile) parent;
    String dirFullPath = createFullPath(parentFile);
    String[] files = cachedListOutputs.getUnchecked(dirFullPath).split("\n");
    for (int j = 2; j < files.length; ++j) {
      String fileName = getFileName(files[j]);
      if (fileName.equals(childFile.getFileName())) {
        childFile.setIndex(j - 1);
        return j - 1;
      }
    }
    return -1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addTreeModelListener(TreeModelListener l) {
    listeners.add(l);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeTreeModelListener(TreeModelListener l) {
    listeners.remove(l);
  }

  private String getNewListOutput(String dirFullPath) {
    String cmd = FtpClientCommands.getCwdCommand(dirFullPath);
    controller.sendToServer(cmd);
    controller.getLineFromServer();
    return mediator.getListResponse();
  }

  public void clearCache() {
    cachedListOutputs.invalidateAll();
  }

}
