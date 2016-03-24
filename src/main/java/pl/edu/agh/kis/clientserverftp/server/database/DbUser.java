package pl.edu.agh.kis.clientserverftp.server.database;

import java.util.Random;

/**
 * Represents informations about user in database.
 * @author Wojciech Kumoń
 */
public final class DbUser {
  private static final int SALT_LENGTH = 16;
  private static Random random = null;
  private final String username;
  private final String password;
  private final String salt;

  /**
   * Randomly generates salt.
   * @return generated salt
   */
  public static String getRandomSalt() {
    byte[] saltBytes = new byte[SALT_LENGTH];
    if (random == null) {
      random = new Random();
    }
    random.nextBytes(saltBytes);
    return new String(saltBytes);
  }

  /**
   * Constructs DbUser.
   * @param username username
   * @param password user password
   * @param salt salt
   */
  public DbUser(String username, String password, String salt) {
    this.username = username;
    this.password = password;
    this.salt = salt;
  }

  /**
   * Returns username.
   * @return username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Returns password.
   * @return password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Returns salt.
   * @return salt
   */
  public String getSalt() {
    return salt;
  }
  
}
