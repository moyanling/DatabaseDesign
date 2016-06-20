package org.mo39.fmbh.databasedesign.model;

import java.util.List;

public class Record {
  
  private String table;
  private Column[] columns;
  
  public Record(String table, List<Column> columns) {
    this.table = table;
    columns.toArray(this.columns);
  }

  public boolean matchColDefinition(Column... columns) {
    // TODO
    return false;
  }

  public void writeToTable() {
    
  }

}
