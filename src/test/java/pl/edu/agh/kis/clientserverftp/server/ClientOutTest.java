package pl.edu.agh.kis.clientserverftp.server;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

public class ClientOutTest {

  private static final String MESSAGE = "message";

  @Test
  public void test() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(MESSAGE.length());
    try (ClientOut out = new ClientOut(baos)) {
      out.println(MESSAGE);
      assertEquals(MESSAGE + System.lineSeparator(), baos.toString());
    }
  }

}
