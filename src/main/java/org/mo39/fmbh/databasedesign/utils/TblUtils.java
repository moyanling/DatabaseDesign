package org.mo39.fmbh.databasedesign.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

public abstract class TblUtils {

  /**
   * Delete one line in a file.
   *
   * @param line
   * @param file
   * @throws IOException
   */
  public static boolean deleteLines(File file, Predicate<CharSequence> p) throws IOException {
    Preconditions.checkArgument(file != null && p != null);
    File tempFile = new File(file.getAbsolutePath() + ".tmp");
    try (BufferedReader br = new BufferedReader(new FileReader(file));
        PrintWriter pw = new PrintWriter(new FileWriter(tempFile))) {

      if (!file.isFile()) {
        throw new Error("Parameter is not an existing file");
      }
      String line = null;

      // Read from the original file and write to the new
      // unless content matches data to be removed.
      while ((line = br.readLine()) != null) {

        if (!p.apply(line)) {
          pw.println(line);
        }
      }
      pw.close();
      br.close();

      // Delete the original file
      if (!file.delete()) {
        throw new Error("Could not delete file");
      }

      // Rename the new file to the filename the original file had.
      if (!tempFile.renameTo(file)) {
        throw new Error("Could not rename file");
      }

    } catch (Exception ex) {
      throw new Error(ex.getMessage());
    }
    return true;
  }

}
