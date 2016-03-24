package pl.edu.agh.kis.clientserverftp.server.commands;

import static org.junit.Assert.*;

import org.junit.Test;

import pl.edu.agh.kis.clientserverftp.server.FtpResponses;

public class SyntaxErrorCommandTest {

  @Test
  public void testCommand() {
    String commandType = "WRONG_COMMAND";
    Command syntaxErrCommand = SyntaxErrorCommand.newInstance(commandType + " ARG1");
    assertEquals(String.format(FtpResponses.SYNTAX_ERROR_500, commandType),
        syntaxErrCommand.execute());
  }

}
