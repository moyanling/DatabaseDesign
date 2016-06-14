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
  private static final Path testByteFile = Paths.get(ROOT_PATH, "testByteFile");
  private static final Path testVarCharFile = Paths.get(ROOT_PATH, "testVarCharFile");

  @Test
  public void testRootPath() {
    Assert.assertTrue(Paths.get(ROOT_PATH).toFile().isDirectory());
  }

  @Test
  public void testByteOutput() throws Exception {
    try (OutputStream out = Files.newOutputStream(testByteFile, StandardOpenOption.APPEND)) {
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
    String str = "Hello";
    try (BufferedWriter bufferedWriter =
        Files.newBufferedWriter(testVarCharFile, StandardCharsets.US_ASCII,StandardOpenOption.APPEND)) {
      Files.write(testVarCharFile, new byte[] {(byte) str.length()}, StandardOpenOption.APPEND);
      bufferedWriter.write(str);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }



}
