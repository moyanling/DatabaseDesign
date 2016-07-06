package org.mo39.fmbh.databasedesign.test.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.mo39.fmbh.databasedesign.model.DataType;

public class TestInfoSchemaUtils {

  @Test
  public void testByteOutputStream() {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      out.write(DataType.parseVarCharToByteArray("YES"));
      out.write(DataType.parseVarCharToByteArray("NULL"));
      Assert.assertArrayEquals(new byte[] {3, 89, 69, 83, 0}, out.toByteArray());
      out.reset();
      out.write(DataType.parseVarCharToByteArray("YES"));
      out.write(DataType.parseVarCharToByteArray("NULL"));
      Assert.assertArrayEquals(new byte[] {3, 89, 69, 83, 0}, out.toByteArray());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}
