package pl.edu.agh.kis.clientserverftp.server.commands;

import static org.junit.Assert.*;

import org.junit.Test;

import pl.edu.agh.kis.clientserverftp.server.FtpResponses;

public class NoopCommandTest {

  @Test
  public void testCommand() {
    Command noopCommand = NoopCommand.newInstance();
    assertEquals(FtpResponses.COMMAND_SUCCESFUL_200, noopCommand.execute());
  }

}
