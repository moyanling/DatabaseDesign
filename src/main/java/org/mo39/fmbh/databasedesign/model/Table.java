package org.mo39.fmbh.databasedesign.model;

import static org.mo39.fmbh.databasedesign.model.DBExceptions.newError;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.ArrayUtils;
import org.mo39.fmbh.databasedesign.framework.InfoSchema;
import org.mo39.fmbh.databasedesign.utils.TblUtils;

import com.google.common.collect.Lists;

public class Table implements Iterable<byte[]> {

  private String schema;
  private String table;
  private List<Column> columns;
  private List<byte[]> records = Lists.newArrayList();

  private Table(String schema, String table, List<Column> columns) {
    this.schema = schema;
    this.table = table;
    this.columns = Collections.unmodifiableList(columns);
  }

  /**
   * Initiate a Row object using column definitions.
   *
   * @param columns
   * @return
   */
  public static Table init(String schema, String table) {
    return new Table(schema, table, InfoSchema.getColumns(schema, table));
  }

  /**
   * Add a new record to this Table object. The record is presented as a byte array.
   *
   * @param values
   */
  public void addRecord(String values) {
    String[] valueArray = values.split(",");
    if (valueArray.length != columns.size()) {
      newError("Adding record: The number of values is not consistent with column definition.");
    }
    byte[] result = new byte[0];
    try {
      for (int i = 0; i < valueArray.length; i++) {
        Column col = columns.get(i);
        String value = valueArray[i].trim();
        if (!DataType.checkDataType(col, value)) {
          newError("Value: " + value + " does not observe datatype: " + col.getDataType().getArg());
        }
        if (!col.getConstraint().impose(schema, table, col)) {
          System.out.println("The constraint fails");
          // TODO Uncomment me after constraint is implement.
          // newError("Value: " + value + " does not observe the constraint "
          // + col.getConstraint().getName() + " for Column " + col.getName());
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
   * Get the first record added to this Row object. The record will be then removed from this Table
   * object.
   *
   * @return
   */
  public byte[] getRecord() {
    return records.remove(0);
  }

  /**
   * Write all records to DB. Then all the records in this Table object is cleared.
   */
  public void writeToDB(String schema, String table) {
    TblUtils.appendRecordsToDB(this, schema, table);
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
    return columns;
  }

}
