package org.mo39.fmbh.databasedesign.model;

import java.util.List;

import org.mo39.fmbh.databasedesign.utils.IOUtils;

public class Record {

  private final String table;
  private final String schema;
  private final Column[] columns;

  public static Record newTemplate(String schema, String table, List<Column> columns) {
    // TODO


    return new RecordTemplate(schema, table, columns);
  }

  public static Record newRecord(String schema, String table, List<Column> columns) {
    return new Record(schema, table, columns);
  }

  public final boolean matchTemplate(List<Column> columns) {
    // TODO
    return false;
  }

  public void writeToTable() {
    IOUtils.writeRecord(this);
  }

  private Record(String schema, String table, List<Column> columns) {
    this.table = table;
    this.schema = schema;
    this.columns = (Column[]) columns.toArray();
  }

  public String getTable() {
    return table;
  }

  public String getSchema() {
    return schema;
  }

  public Column[] getColumns() {
    return columns;
  }

  private static class RecordTemplate extends Record {

    public RecordTemplate(String schema, String table, List<Column> columns) {
      super(schema, table, columns);
    }

  }



}
