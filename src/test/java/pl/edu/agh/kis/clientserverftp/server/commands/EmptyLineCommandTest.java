package pl.edu.agh.kis.clientserverftp.server.commands;

import static org.junit.Assert.*;

import org.junit.Test;

import pl.edu.agh.kis.clientserverftp.server.FtpResponses;

public class EmptyLineCommandTest {

  @Test
  public void test() {
    Command emptyLineCommand = EmptyLineCommand.newInstance();
    assertEquals(FtpResponses.EMPTY_LINE_500, emptyLineCommand.execute());
  }

}
