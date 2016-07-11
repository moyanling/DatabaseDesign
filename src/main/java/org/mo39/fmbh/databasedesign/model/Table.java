package org.mo39.fmbh.databasedesign.model;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.mo39.fmbh.databasedesign.model.DBExceptions.AddRecordException;
import org.mo39.fmbh.databasedesign.model.DBExceptions.ConstraintViolationException;
import org.mo39.fmbh.databasedesign.utils.IOUtils;
import org.mo39.fmbh.databasedesign.utils.InfoSchemaUtils;

import com.google.common.collect.Lists;

public class Table implements Iterable<String> {

  private String schema = null;
  private String table = null;
  private List<Column> columns = null;
  private List<String> records = Lists.newArrayList();

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
    return new Table(schema, table, InfoSchemaUtils.getColumns(schema, table));
  }

  /**
   * Initiate a Row object using column definitions.
   *
   * @param columns
   * @return
   */
  public static Table init(String schema, String table, List<Column> cols) {
    return new Table(schema, table, cols);
  }

  /**
   * Add a new record to this Table object. The record is presented as a byte array.
   *
   * @param values
   * @throws DBExceptions
   */
  public void addRecord(String values) throws DBExceptions {
    String[] valueArray = values.split(",");
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
      if (!col.getConstraint().impose(schema, table, col, value)) {
        throw new ConstraintViolationException(
            "Value: " + value + " does not observe the constraint " + col.getConstraint().getName()
                + " for Column " + col.getName());
      }
    }
    records.add(values);
  }

  /**
   * Return the number of records.
   *
   * @return
   */
  public int size() {
    return records.size();
  }

  /**
   * Get the first record added to this Row object. The record will be then removed from this Table
   * object.
   *
   * @return
   */
  public String getRecord() {
    return records.remove(0);
  }

  public String getSchema() {
    return schema;
  }

  public String getTable() {
    return table;
  }

  /**
   * Write all records to DB. Then all the records in this Table object is cleared.
   *
   * @throws IOException
   */
  public void writeToDB() throws IOException {
    IOUtils.appendRecordsToDB(this);
  }

  /**
   * An iterator help to traverse the records.
   *
   */
  @Override
  public Iterator<String> iterator() {
    return new Iterator<String>() {

      @Override
      public boolean hasNext() {
        return records.size() > 0;
      }

      @Override
      public String next() {
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
