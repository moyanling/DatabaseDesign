package org.mo39.fmbh.databasedesign.model;

import static org.mo39.fmbh.databasedesign.model.DBExceptions.newError;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.ArrayUtils;
import org.mo39.fmbh.databasedesign.utils.TblUtils;

import com.google.common.collect.Lists;

public class Row implements Iterable<byte[]> {

  private List<Column> colDefs;
  private List<byte[]> records = Lists.newArrayList();

  private Row(List<Column> colDefs) {
    this.colDefs = colDefs;
  }

  /**
   * Initiate a Row object using column definitions.
   *
   * @param colDefs
   * @return
   */
  public static Row init(List<Column> colDefs) {
    return new Row(colDefs);
  }

  /**
   * Add one new record to this Row object. The record is presented as a byte array.
   *
   * @param values
   */
  public void addRecord(String values) {
    String[] valueArray = values.split(",");
    if (valueArray.length != colDefs.size()) {
      newError("Adding record: The number of values is not consistent with column definition.");
    }
    byte[] result = new byte[0];
    try {
      for (int i = 0; i < valueArray.length; i++) {
        Column col = colDefs.get(i);
        String value = valueArray[i].trim();
        if (!DataType.checkDataType(col, value)) {
          newError("Value: " + value + " does not observe datatype: " + col.getDataType().getArg());
        }
        byte[] bytes = (byte[]) DataType.class
            .getMethod(col.getDataType().getParseToByteArray(), String.class).invoke(null, value);
        result = ArrayUtils.addAll(result, bytes);
      }
      records.add(result);
    } catch (Exception e) {
      newError(e);
    }
  }

  /**
   * Get the first record added to this Row object. The record will be than removed from the Row.
   *
   * @return
   */
  public byte[] getRecord() {
    return records.remove(0);
  }

  /**
   * Write all records to DB. Then all the records in this Row object is cleared.
   */
  public void writeToDB(String schema, String table) {
    for (int i = 0; i < records.size(); i++) {
      TblUtils.appendRecordTotbl(getRecord(), schema, table);
    }
  }

  /**
   * An iterator help to traverse the records.
   *
   */
  @Override
  public Iterator<byte[]> iterator() {
    return new Iterator<byte[]>() {

      private int index = 0;

      @Override
      public boolean hasNext() {
        return index < records.size();
      }

      @Override
      public byte[] next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        return records.get(index);
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }

    };
  }

  public List<Column> getColumns() {
    return Collections.unmodifiableList(colDefs);
  }

}
