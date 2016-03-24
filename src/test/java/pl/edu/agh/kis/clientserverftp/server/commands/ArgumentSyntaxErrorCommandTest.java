package pl.edu.agh.kis.clientserverftp.server.commands;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pl.edu.agh.kis.clientserverftp.server.FtpResponses;

public class ArgumentSyntaxErrorCommandTest {

  @Test
  public void testArgSyntaxErrorCommand() {
    Command argSyntaxErrorCommand = ArgumentSyntaxErrorCommand.newInstance();
    assertEquals(FtpResponses.ARGUMENT_SYNTAX_ERROR_501, argSyntaxErrorCommand.execute());
  }

}
