package org.mo39.fmbh.databasedesign.utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Paths;

import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.InfoSchema;

/**
 * //TODO not working.
 *
 * @author Jihan Chen
 *
 */
public class DBLocker {

  private FileLock fc = null;

  public void acquireLock() {
    RandomAccessFile file;
    try {
      file = new RandomAccessFile(
          Paths.get(InfoSchema.getArchiveRoot(), InfoSchema.getFileLock()).toFile(), "rw");
      while (true) {
        fc = file.getChannel().tryLock();
        if (fc != null) {
          break;
        } else {
          System.err.println("I am sleeping");
          Thread.sleep(100);
        }
      }
    } catch (Exception e) {
      DBExceptions.newError(e);
    }
  }

  public void releaseLock() {
    try {
      if (fc != null) {
        fc.release();
        fc.acquiredBy().close();
      }
    } catch (IOException e) {
      DBExceptions.newError(e);
    }
  }
}
