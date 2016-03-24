package pl.edu.agh.kis.clientserverftp.client.controller;

/**
 * FTP client commands
 * @author Wojciech Kumoń
 *
 */
public class FtpClientCommands {
  
  public static String getUserCommand(String username) {
    return "USER " + username;
  }
  
  public static String getPassCommand(String password) {
    return "PASS " + password;
  }
  
  public static String getQuitCommand() {
    return "QUIT";
  }
  
  public static String getNoopCommand() {
    return "NOOP";
  }
  
  public static String getPasvCommand() {
    return "PASV";
  }
  
  public static String getStorCommand(String filePath) {
    return "STOR " + filePath;
  }
  
  public static String getRetrCommand(String filePath) {
    return "RETR " + filePath;
  }
  
  public static String getAppeCommand(String filePath) {
    return "APPE " + filePath;
  }
  
  public static String getAborCommand() {
    return "ABOR";
  }
  
  public static String getDeleCommand(String filePath) {
    return "DELE " + filePath;
  }
  
  public static String getRmdCommand(String dirPath) {
    return "RMD " + dirPath;
  }
  
  public static String getMkdCommand(String dirPath) {
    return "MKD " + dirPath;
  }
  
  public static String getPwdCommand() {
    return "PWD";
  }
  
  public static String getListCommand() {
    return "LIST";
  }
  
  public static String getCwdCommand(String dirPath) {
    return "CWD " + dirPath;
  }
  
  public static String getChmodCommand(String filePath, byte permissions) {
    return "CHMOD " + filePath + " " + permissions;
  }
  
  private FtpClientCommands() {}

  public static String getBinaryModeCommand() {
    return "TYPE I";
  }
  
}
