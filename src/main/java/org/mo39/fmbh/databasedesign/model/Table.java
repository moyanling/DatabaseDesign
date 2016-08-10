package org.mo39.fmbh.databasedesign.model;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.mo39.fmbh.databasedesign.model.Constraint.PrimaryKey;
import org.mo39.fmbh.databasedesign.model.DBExceptions.AddRecordException;
import org.mo39.fmbh.databasedesign.model.DBExceptions.ConstraintViolationException;
import org.mo39.fmbh.databasedesign.model.Table.Record;
import org.mo39.fmbh.databasedesign.utils.IOUtils;
import org.mo39.fmbh.databasedesign.utils.InfoSchemaUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class Table implements Iterable<Record> {

  private String schema = null;
  private String table = null;
  private List<Column> columns = null;
  private List<Record> records = Lists.newArrayList();

  private Table(String schema, String table, List<Column> columns) {
    this.schema = schema;
    this.table = table;
    this.columns = Collections.unmodifiableList(columns);
  }

  /**
   * Initiate a new Table object using column definitions.
   *
   * @param columns
   * @return
   */
  public static Table init(String schema, String table) {
    return new Table(schema, table, InfoSchemaUtils.getColumns(schema, table));
  }

  /**
   * Initiate a new Table object using column definitions.
   *
   * @param columns
   * @return
   */
  public static Table init(String schema, String table, List<Column> cols) {
    return new Table(schema, table, cols);
  }

  /**
   * Parse a tbl file into a Table object.<br>
   * This function is not supposed to throw any exception.
   *
   * @param schema
   * @param table
   * @return
   * @throws DBExceptions
   */
  public static Table retrieve(String schema, String table) throws DBExceptions {
    Table t = Table.init(schema, table);
    try {
      ByteBuffer bb = ByteBuffer.wrap(Files.toByteArray(IOUtils.tblRef(schema, table)));
      while (bb.hasRemaining()) {
        List<String> values = Lists.newArrayList();
        for (Column col : t.columns) {
          values.add(col.hitsBuffer(bb).toString());
        }
        String record = Joiner.on(",").join(values);
        t.records.add(t.new Record(record));
      }
    } catch (IOException | SecurityException | IllegalArgumentException e) {
      DBExceptions.newError(e);
    }
    return t;
  }

  private int primaryKeyCounter = 0;
  private String primaryKeyValue = null;

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
      if (col.getConstraint() instanceof PrimaryKey) {
        if (primaryKeyValue == null || value.equals(primaryKeyValue)) {
          primaryKeyValue = value;
          primaryKeyCounter += 1;
          if (primaryKeyCounter >= 2) {
            throw new ConstraintViolationException(
                "Value: " + value + " does not observe the constraint "
                    + col.getConstraint().getName() + " for Column " + col.getName());
          }
        }
      }
      if (!col.getConstraint().impose(schema, table, col, value)) {
        throw new ConstraintViolationException(
            "Value: " + value + " does not observe the constraint " + col.getConstraint().getName()
                + " for Column " + col.getName());
      }
    }
    records.add(this.new Record(values));
  }

  /**
   * Return the number of records.
   *
   * @return
   */
  public int size() {
    return records.size();
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
  public Iterator<Record> iterator() {
    return new Iterator<Record>() {

      @Override
      public boolean hasNext() {
        return records.size() > 0;
      }

      @Override
      public Record next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        return records.remove(0);
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

  public List<Record> getRecords() {
    return records;
  }

  public class Record {

    private String values;

    private Record(String values) {
      this.values = values;
    }

    /**
     * Has value at index
     *
     * @param v
     * @param index
     * @return
     */
    public boolean hasValueAtIndex(String v, int index) {
      String[] vs = values.split(",");
      return vs[index].equals(v);
    }

    /**
     * Replace value at index
     *
     * @param value
     * @param i
     * @return
     */
    public void replaceValueAtIndex(String value, int i) {
      String[] valueArr = values.split(",");
      valueArr[i] = value;
      values = Joiner.on(",").join(valueArr);
    }

    @Override
    public String toString() {
      return values;
    }
  }

}
