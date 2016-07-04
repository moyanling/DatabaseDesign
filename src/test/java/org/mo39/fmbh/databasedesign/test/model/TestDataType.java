package org.mo39.fmbh.databasedesign.test.model;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;
import org.mo39.fmbh.databasedesign.model.DataType;


public class TestDataType {

  @Test
  public void testValues() {
    Assert.assertArrayEquals(new byte[] {3, 89, 69, 83}, DataType.parseVarCharToByteArray("YES"));
    Assert.assertArrayEquals(new byte[] {0}, DataType.parseVarCharToByteArray(""));
  }

  @Test
  public void testParseTo() throws Exception {
    Assert.assertEquals(1,
        Class.forName("java.lang.Integer").getMethod("parseInt", String.class).invoke(null, "1"));
    Assert.assertEquals((byte) 1,
        Class.forName("java.lang.Byte").getMethod("parseByte", String.class).invoke(null, "1"));

  }

  @Test
  public void testParseVarCharToByteArray() {
    Assert.assertArrayEquals(new byte[] {11, 72, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100},
        DataType.parseVarCharToByteArray("Hello world"));
  }

  @Test
  public void testParseVarCharFromByteBuffer() {
    Assert.assertEquals("Hello world", DataType.parseVarCharFromByteBuffer(
        ByteBuffer.wrap(new byte[] {11, 72, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100})));
  }

  @Test
  public void testParseVarCharToByteArrayUsingEmptyString() {
    Assert.assertArrayEquals(new byte[] {0}, DataType.parseVarCharToByteArray(""));
  }

  @Test
  public void testReadRecord() {
    byte[] record1 = {127, 0, 0, 0, 2, 11, 72, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100};
    ByteBuffer bb1 = ByteBuffer.wrap(record1);
    Assert.assertEquals(Byte.valueOf("127"), DataType.parseByteFromByteBuffer(bb1));
    Assert.assertEquals(Integer.valueOf("2"), DataType.parseIntFromByteBuffer(bb1));
    Assert.assertEquals("Hello world", DataType.parseVarCharFromByteBuffer(bb1));
  }

  @Test
  public void testSwitch() {
    StringBuilder sb = new StringBuilder();
    String a = "1";
    switch (a) {
      case "1":
        if (a.equals("1")) {
          sb.append("Hello ");
        }
      default:
        sb.append("world");
    }
    Assert.assertEquals("Hello world", sb.toString());
  }


}
