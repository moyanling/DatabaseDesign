package org.mo39.fmbh.databasedesign.test.util;

import org.junit.Assert;
import org.junit.Test;
import org.mo39.fmbh.databasedesign.utils.DataTypeParsingUtils;

public class TestDataTypeParsingUtils {

  @Test
  public void testParseVarCharToBytes() {
    Assert.assertArrayEquals(new byte[] {11, 72, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100},
        DataTypeParsingUtils.parseVarCharToBytes("Hello world"));
  }

  @Test
  public void testParseVarCharFromBytes() {
    Assert.assertEquals("Hello world", DataTypeParsingUtils.parseVarCharFromBytes(
        new byte[] {11, 72, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100}));
  }

  @Test
  public void testParseVarCharToBytesUsingEmptyString() {
    Assert.assertArrayEquals(new byte[] {0}, DataTypeParsingUtils.parseVarCharToBytes(""));
  }

}
