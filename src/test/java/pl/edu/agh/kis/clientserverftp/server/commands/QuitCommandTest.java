package pl.edu.agh.kis.clientserverftp.server.commands;

import static org.junit.Assert.*;

import org.junit.Test;

import pl.edu.agh.kis.clientserverftp.server.FtpResponses;

public class QuitCommandTest {

  @Test
  public void testCommand() {
    Command quitCommand = QuitCommand.newInstance();
    assertEquals(FtpResponses.BYE_221, quitCommand.execute());
  }

}
