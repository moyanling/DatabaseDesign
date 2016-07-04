package org.mo39.fmbh.databasedesign.utils;

import java.util.List;

import org.mo39.fmbh.databasedesign.model.Column;

/**
 * Helper class to update info schema.
 *
 * @author Jihan Chen
 *
 */
class UpdateInfoSchema {
  
//  private static File schemata = FileUtils.tblRef(InfoSchema.getInfoSchema(), InfoSchema.getSchemata());
//  private static File tables = FileUtils.tblRef(InfoSchema.getInfoSchema(), InfoSchema.getTables());
//  private static File columns = FileUtils.tblRef(InfoSchema.getInfoSchema(), InfoSchema.getColumns());

  /**
   * Update information schema when new schema is created.
   *
   * @param schema
   */
  public static void atCreatingSchema(String schema) {
    // TODO update information schema when a schema is created
  }

  /**
   * Update information schema when new table is created.
   *
   * @param schema
   * @param table
   * @param cols
   */
  public static void atCreatingTable(String schema, String table, List<Column> cols) {
    // TODO update information schema when a table is created
  }

  /**
   * Update information schema when table is dropped.
   *
   * @param schema
   * @param table
   * @return
   */
  public static void atDroppingTable(String schema, String table) {
    // TODO update information schema when a table is deleted
  }

  /**
   * Update information schema when schema is deleted.
   *
   * @param schema
   * @return
   */
  public static void atDeletingSchema(String schema) {
    // TODO update information schema when a schema is deleted
  }

  public static void atAppendingNewRecord() {
    // TODO update information schema when a record is appended to DB
  }
}
