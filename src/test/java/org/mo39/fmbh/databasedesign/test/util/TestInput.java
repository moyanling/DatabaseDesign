package org.mo39.fmbh.databasedesign.test.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

public class TestInput {

  private static final String ROOT_PATH = "src\\test\\resources\\org\\mo39\\fmbh\\databasedesign";
  private static final Path testByteFile = Paths.get(ROOT_PATH, "testByteFile");
  private static final Path testVarCharFile = Paths.get(ROOT_PATH, "testVarCharFile");

  @Test
  public void testRootPath() {
    Assert.assertTrue(Paths.get(ROOT_PATH).toFile().isDirectory());
  }

  @Test
  public void testByteOutput() throws Exception {
    int len = 16;
    byte[] cbuf = new byte[len];
    try (FileInputStream in = new FileInputStream(testByteFile.toFile())) {
      // Read all 16 (cbuf.len) bytes into cbuf.
      in.read(cbuf);
      for (int i = 0; i < len; i++) {
        Assert.assertEquals(i, cbuf[i]);
      }
      // Read the last byte.
      Assert.assertEquals(127, in.read());
      // Reaches the end of the data
      Assert.assertEquals(-1, in.read());
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }

  }

  @Test
  public void testVarCharOutput() throws Exception {
    try (BufferedReader bufferedReader =
        Files.newBufferedReader(testVarCharFile, StandardCharsets.US_ASCII)) {
      if (bufferedReader.ready()) {
        int length = bufferedReader.read();
        char[] cbuf = new char[length];
        bufferedReader.read(cbuf);
        Assert.assertEquals("Hello", String.valueOf(cbuf));
      }

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }



}
