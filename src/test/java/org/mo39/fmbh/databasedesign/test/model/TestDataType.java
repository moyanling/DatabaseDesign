package org.mo39.fmbh.databasedesign.test.model;

import org.junit.Assert;
import org.junit.Test;


public class TestDataType {

  @Test
  public void testParseTo() throws Exception {
    Assert.assertEquals(1,
        Class.forName("java.lang.Integer").getMethod("parseInt", String.class).invoke(null, "1"));
    Assert.assertEquals((byte) 1,
        Class.forName("java.lang.Byte").getMethod("parseByte", String.class).invoke(null, "1"));

  }

}
