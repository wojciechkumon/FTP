package pl.edu.agh.kis.clientserverftp.server.commands;

import static org.junit.Assert.*;

import java.util.regex.Pattern;

import org.junit.Test;

public class CommandPatternsTest {

  private boolean matches(Pattern pattern, String string) {
    return pattern.matcher(string).matches();
  }

  private void testOneArgPattern(Pattern pattern, String command) {
    assertTrue(matches(pattern, command + " user123"));
    assertFalse(matches(pattern, "NOT" + command));
    assertFalse(matches(pattern, command));
    assertFalse(matches(pattern, command + " "));
    assertFalse(matches(pattern, command + "  "));
    assertFalse(matches(pattern, command + " user pass"));
  }

  @Test
  public void testUserPattern() {
    testOneArgPattern(CommandPatterns.USER_PATTERN, "USER");
  }

  @Test
  public void testPasswordPattern() {
    assertTrue(matches(CommandPatterns.PASS_PATTERN, "PASS" + " user123"));
    assertTrue(matches(CommandPatterns.PASS_PATTERN, "PASS"));
    assertFalse(matches(CommandPatterns.PASS_PATTERN, "NOTPASS"));
  }

  private void testNoArgsPattern(Pattern pattern, String command) {
    assertTrue(matches(pattern, command));
    assertFalse(matches(pattern, "NOT" + command));
    assertFalse(matches(pattern, command + " "));
    assertFalse(matches(pattern, command + " arg"));
  }

  @Test
  public void testQuitPattern() {
    testNoArgsPattern(CommandPatterns.QUIT_PATTERN, "QUIT");
  }

  @Test
  public void testNoopPattern() {
    testNoArgsPattern(CommandPatterns.NOOP_PATTERN, "NOOP");
  }

  @Test
  public void testPasvPattern() {
    testNoArgsPattern(CommandPatterns.PASV_PATTERN, "PASV");
  }

  @Test
  public void testAborPattern() {
    testNoArgsPattern(CommandPatterns.ABOR_PATTERN, "ABOR");
  }

  @Test
  public void testPwdPattern() {
    testNoArgsPattern(CommandPatterns.PWD_PATTERN, "PWD");
  }

  @Test
  public void testListPattern() {
    assertTrue(matches(CommandPatterns.LIST_PATTERN, "LIST"));
    assertFalse(matches(CommandPatterns.LIST_PATTERN, "NOT" + "LIST"));
  }

  private void testOneFileNameArgPattern(Pattern pattern, String command) {
    assertTrue(matches(pattern, command + " file123"));
    assertTrue(matches(pattern, command + " file123.txt"));
    assertTrue(matches(pattern, command + " dir/dir2/file123.sth.txt"));
    assertFalse(matches(pattern, "NOT" + command));
    assertFalse(matches(pattern, command));
    assertFalse(matches(pattern, command + " "));
    assertFalse(matches(pattern, command + "  "));
    assertFalse(matches(pattern, command + " file1 file2"));
  }

  @Test
  public void testStorPattern() {
    testOneFileNameArgPattern(CommandPatterns.STOR_PATTERN, "STOR");
  }

  @Test
  public void testRetrPattern() {
    testOneFileNameArgPattern(CommandPatterns.RETR_PATTERN, "RETR");
  }

  @Test
  public void testDelePattern() {
    testOneFileNameArgPattern(CommandPatterns.DELE_PATTERN, "DELE");
  }

  @Test
  public void testRmdPattern() {
    testOneFileNameArgPattern(CommandPatterns.RMD_PATTERN, "RMD");
  }

  @Test
  public void testCwdPattern() {
    testOneFileNameArgPattern(CommandPatterns.CWD_PATTERN, "CWD");
  }

  @Test
  public void testChmodPattern() {
    Pattern pattern = CommandPatterns.CHMOD_PATTERN;
    assertTrue(matches(pattern, "CHMOD file123.txt 11"));
    assertTrue(matches(pattern, "CHMOD file123.sth.txt 02"));
    assertFalse(matches(pattern, "NOTCHMOD"));
    assertFalse(matches(pattern, "CHMOD"));
    assertFalse(matches(pattern, "CHMOD "));
    assertFalse(matches(pattern, "CHMOD "));
    assertFalse(matches(pattern, "CHMOD file"));
    assertFalse(matches(pattern, "CHMOD file 51"));
    assertFalse(matches(pattern, "CHMOD file 000"));
    assertFalse(matches(pattern, "CHMOD file 00 0"));
  }

}
