package org.mo39.fmbh.databasedesign.test.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class TestTblUtils {

  @Test
  public void testObjectToString() {
    Object obj = new Integer(123);
    Assert.assertEquals(obj.toString(), "123");

    Object obj1 = 1234;
    Assert.assertEquals(obj1.toString(), "1234");
  }

  @Test
  public void testByteArrayOutputStream() throws IOException {
    ByteArrayOutputStream x = new ByteArrayOutputStream();
    x.write(new byte[] {3, 4, 5, 6, 7});
    x.reset();
    x.close();
  }

}
