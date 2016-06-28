package org.mo39.fmbh.databasedesign.model;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.common.collect.Lists;

public class Row implements Iterable<byte[]> {

  private List<Column> colDefs;
  private List<byte[]> records;

  private Row(List<Column> colDefs) {
    this.colDefs = colDefs;
  }

  public static Row init(List<Column> colDefs) {
    return new Row(colDefs);
  }

  public void addRecord(String values) {
    String[] valueArray = values.split(",");
    if (valueArray.length != colDefs.size()) {
      DBExceptions.newError("The number of values is not consistent with column definition.");
    }
    List<byte[]> temp = Lists.newArrayList();
    for (int i = 0; i < valueArray.length; i++) {
      Column col = colDefs.get(i);
      String value = valueArray[i].trim();



    }
  }

  public byte[] getRecord(int i) {
    return records.get(i);
  }

  /**
   * An iterator help to traverse the records.
   * 
   */
  @Override
  public Iterator<byte[]> iterator() {
    return new Iterator<byte[]>() {

      private int index = 0;

      public boolean hasNext() {
        return index < records.size();
      }

      public byte[] next() {
        if (!hasNext())
          throw new NoSuchElementException();
        return records.get(index);
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }

    };
  }

}
