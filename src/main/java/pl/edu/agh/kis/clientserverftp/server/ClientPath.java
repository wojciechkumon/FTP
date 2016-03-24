package pl.edu.agh.kis.clientserverftp.server;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Holds user current path.
 * 
 * @author Wojciech Kumoń
 *
 */
class ClientPath {
  private Path path = Paths.get("");

  /**
   * Returns current client path.
   * 
   * @return current client path
   */
  Path getClientPath() {
    return path;
  }

  /**
   * Returns current real server path.
   * 
   * @return current real server path
   */
  Path getRealPath() {
    try {
      return Paths.get(ServerProperties.getFtpDirectoryPath()).toRealPath().resolve(path);
    } catch (IOException e) {
      throw new RuntimeException("Wrong server root path set in properties file.");
    }
  }

  /**
   * Changes directory
   * 
   * @param dir new directory
   */
  void changeDirectory(String dir) {
    if (path.toString().equals("") && dir.toString().equals("..")) {
      return;
    }
    path = Paths.get(dir);
  }

  /**
   * Returns resolved absolute server path.
   * 
   * @param pathToResolve path to resolve
   * @return resolved absolute server path
   */
  Path getAbsoluteResolvedPath(Path pathToResolve) {
    String pathString = pathToResolve.toString();
    if (pathString.startsWith("/")) {
      return getRootPath().resolve(removeSlash(pathString)).normalize();
    }
    return getRealPath().resolve(pathToResolve).normalize();
  }

  /**
   * Returns relativised server path.
   * 
   * @param pathToRelativize path to relativise
   * @return relativised server path
   */
  static Path getFtpRelativePath(Path pathToRelativize) {
    return getRootPath().relativize(pathToRelativize.toAbsolutePath()).normalize();
  }

  private static Path getRootPath() {
    return Paths.get(ServerProperties.getFtpDirectoryPath()).toAbsolutePath().normalize();
  }

  private String removeSlash(String pathString) {
    return pathString.substring(1);
  }

}
