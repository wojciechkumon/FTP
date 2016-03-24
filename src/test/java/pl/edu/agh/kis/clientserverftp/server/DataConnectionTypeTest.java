package pl.edu.agh.kis.clientserverftp.server;

import static org.junit.Assert.*;

import org.junit.Test;

public class DataConnectionTypeTest {

  @Test
  public void test() {
    assertEquals(3, DataConnectionType.values().length);
  }

}
