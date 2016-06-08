package org.mo39.fmbh.databasedesign.framework;

/**
 * This presents the current status of database. Includes but not limited to the schema, table and
 * SQL currently using. Singleton is employed for further possible implement on multi-threads.
 *
 * @author Jihan Chen
 *
 */
public enum Status {

  INSTANCE;

  private String currentSql;
  private String currentTable;
  private String currentSchema;

  public void endSql() {
    currentSql = null;
  }

  public boolean hasActiveTable() {
    return currentTable == null;
  }

  public boolean hasActiveSchema() {
    return currentSchema == null;
  }

  public String getCurrentSql() {
    return currentSql;
  }

  public void setCurrentSql(String currentSql) {
    this.currentSql = currentSql;
  }

  public String getCurrentTable() {
    return currentTable;
  }

  public void setCurrentTable(String currentTable) {
    this.currentTable = currentTable;
  }

  public String getCurrentSchema() {
    return currentSchema;
  }

  public void setCurrentSchema(String currentSchema) {
    this.currentSchema = currentSchema;
  }

}
