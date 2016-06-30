package org.mo39.fmbh.databasedesign.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.ArrayUtils;
import org.mo39.fmbh.databasedesign.framework.InfoSchema;
import org.mo39.fmbh.databasedesign.model.DBExceptions.AddRecordException;
import org.mo39.fmbh.databasedesign.model.DBExceptions.ConstraintViolationException;
import org.mo39.fmbh.databasedesign.utils.TblUtils;

import com.google.common.collect.Lists;

public class Table implements Iterable<byte[]> {

  private String schema = null;
  private String table = null;
  private List<Column> columns = null;
  private List<byte[]> records = Lists.newArrayList();;

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
   * @throws DBExceptions
   */
  public void addRecord(String values) throws DBExceptions {
    String[] valueArray = values.split(",");
    byte[] result = new byte[0];
    if (valueArray.length != columns.size()) {
      throw new AddRecordException(
          "Adding record: The number of values is not consistent with column definition.");
    }
    for (int i = 0; i < valueArray.length; i++) {
      Column col = columns.get(i);
      String value = valueArray[i].trim();
      if (!DataType.checkDataType(col, value)) {
        throw new ConstraintViolationException(
            "Value: " + value + " does not observe datatype: " + col.getDataType().getArg());
      }
      if (!col.getConstraint().impose(schema, table, col)) {
        System.out.println("The constraint fails");
        // TODO Uncomment me after constraint is implement.
        // newError("Value: " + value + " does not observe the constraint "
        // + col.getConstraint().getName() + " for Column " + col.getName());
      }
      try {
        byte[] bytes = (byte[]) DataType.class
            .getMethod(col.getDataType().getParseToByteArray(), String.class).invoke(null, value);
        result = ArrayUtils.addAll(result, bytes);
      } catch (Exception e) {
        DBExceptions.newError(e);
      }
    }
    records.add(result);
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

      @Override
      public boolean hasNext() {
        return records.size() > 0;
      }

      @Override
      public byte[] next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        return getRecord();
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
