package org.mo39.fmbh.databasedesign.framework;

/**
 * This presents the current status of database. Includes but not limited to the schema, table and
 * SQL currently using. The class is run in threads. In a single thread, it should not be create
 * twice. When the first time getInstance is called, it could create a new instance will null values
 * in its fields. Otherwise return the already existed instance.
 *
 * @author Jihan Chen
 *
 */
public class Status {

  private Status() {
    count++;
  }

  private String currentCmd;
  private String currentTable;
  private String currentSchema;

  private static Status holder;
  private static int count = 0;

  public static Status getInstance() {
    if (count == 0) {
      holder = new Status();
      return holder;
    } else {
      return holder;
    }
  }

  public void endRunCmd() {
    holder.currentCmd = null;
  }

  public boolean hasActiveTable() {
    return holder.currentTable == null;
  }

  public boolean hasActiveSchema() {
    return holder.currentSchema == null;
  }

  public String getCurrentCmd() {
    return holder.currentCmd;
  }

  public void setCurrentCmd(String currentSql) {
    holder.currentCmd = currentSql;
  }

  public String getCurrentTable() {
    return holder.currentTable;
  }

  public void setCurrentTable(String currentTable) {
    holder.currentTable = currentTable;
  }

  public String getCurrentSchema() {
    return holder.currentSchema;
  }

  public void setCurrentSchema(String currentSchema) {
    holder.currentSchema = currentSchema;
  }

}
