package org.mo39.fmbh.databasedesign.test.io;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Assert;
import org.junit.Test;

public class TestOutput {

  private static final String ROOT_PATH = "src\\test\\resources\\org\\mo39\\fmbh\\databasedesign";
  private static final Path TEST_BYTE_FILE = Paths.get(ROOT_PATH, "testByteFile");
  private static final Path TEST_VAR_CHAR_FILE = Paths.get(ROOT_PATH, "testVarCharFile");

  @Test
  public void testRootPath() {
    Assert.assertTrue(Paths.get(ROOT_PATH).toFile().isDirectory());
  }

  @Test
  public void testByteOutput() throws Exception {
    if (TEST_BYTE_FILE.toFile().exists()) {
      TEST_BYTE_FILE.toFile().delete();
    }
    try (OutputStream out = Files.newOutputStream(TEST_BYTE_FILE, StandardOpenOption.CREATE)) {
      for (int i = 0; i < 16; i++) {
        out.write(i);
      }
      // Range from -128 to 127.
      out.write(127);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }

  }

  @Test
  public void testVarCharOutput() throws Exception {
    if (TEST_VAR_CHAR_FILE.toFile().exists()) {
      TEST_VAR_CHAR_FILE.toFile().delete();
    }
    String str = "Hello";
    try (BufferedWriter bufferedWriter = Files.newBufferedWriter(TEST_VAR_CHAR_FILE,
        StandardCharsets.US_ASCII, StandardOpenOption.CREATE)) {
      Files.write(TEST_VAR_CHAR_FILE, new byte[] {(byte) str.length()}, StandardOpenOption.APPEND);
      bufferedWriter.write(str);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }



}
