package org.mo39.fmbh.databasedesign.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Paths;

import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.InfoSchema;

/**
 * //TODO not working.
 *
 * @author Jihan Chen
 *
 */
@SuppressWarnings("resource")
public class DBLocker {

  private static FileChannel fc;

  {
    try {
      fc = new RandomAccessFile(
          Paths.get(InfoSchema.getArchiveRoot(), InfoSchema.getFileLock()).toFile(), "rw")
              .getChannel();
    } catch (FileNotFoundException e) {
      DBExceptions.newError(e);
    }
  }

  private static FileLock fl = null;

  public static void acquireLock() {
    try {
      while (true) {
        try {
          fl = fc.tryLock();
          break;
        } catch (OverlappingFileLockException e) {
          System.err.println("I am sleeping");
          Thread.sleep(100);
        }
      }
    } catch (Exception e) {
      DBExceptions.newError(e);
    }
  }

  public static void releaseLock() {
    try {
      if (fl != null) {
        fl.release();
        fl.close();
        fc.close();
      }
    } catch (IOException e) {
      DBExceptions.newError(e);
    }
  }
}
