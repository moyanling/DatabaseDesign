package org.mo39.fmbh.databasedesign.utils;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;

import org.mo39.fmbh.databasedesign.framework.InfoSchema;
import org.mo39.fmbh.databasedesign.model.Column;
import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.DataType;

import com.google.common.collect.Sets;
import com.google.common.io.Files;



/**
 * Helper class used to read information from info schema.
 *
 * @author Jihan Chen
 *
 */
class InfoSchemaUtils {
  /**
   * Get schemas from SCHEMATA. This function is not relaying on the definition of SCHEMATA table.
   * If the definition is changed, this function need to be changed as well.
   *
   * @return
   */
  public static Set<String> getSchemas() {
    Set<String> schemas = Sets.newHashSet();
    File tbl = FileUtils.tblRef(InfoSchema.getInfoSchema(), InfoSchema.getSchemata());
    try {
      byte[] fileContent = Files.toByteArray(tbl);
      ByteBuffer bb = ByteBuffer.wrap(fileContent);
      while (bb.hasRemaining()) {
        schemas.add(DataType.parseVarCharFromByteBuffer(bb));
      }
    } catch (IOException e) {
      DBExceptions.newError(e);
    }
    return schemas;
  }

  /**
   * Get tables from TABLES. This function is not relaying on the definition of TABLES table. If the
   * definition is changed, this function need to be changed as well.
   *
   * @param schema
   * @return
   */
  public static Set<String> getTables(String schema) {
    checkArgument(schema != null);
    Set<String> tables = Sets.newHashSet();
    File tbl = FileUtils.tblRef(InfoSchema.getInfoSchema(), InfoSchema.getTables());
    try {
      String schemaName;
      String tableName;
      byte[] fileContent = Files.toByteArray(tbl);
      ByteBuffer bb = ByteBuffer.wrap(fileContent);
      while (bb.hasRemaining()) {
        schemaName = DataType.parseVarCharFromByteBuffer(bb);
        tableName = DataType.parseVarCharFromByteBuffer(bb);
        bb.position(bb.position() + 4);// Skip read the number of rows.
        if (schemaName.equals(schema)) {
          tables.add(tableName);
        }
      }
    } catch (IOException e) {
      DBExceptions.newError(e);
    }
    return tables;
  }

  /**
   * Get columns from COLUMNS. This function is not relaying on the definition of COLUMNS table. If
   * the definition is changed, this function need to be changed as well.
   * 
   * @param schema
   * @param table
   * @return
   */
  public static List<Column> getColumns(String schema, String table) {
    checkArgument(schema != null && table != null);
    // Convert a byte array to a Column object.


    return null;
  }



  /**
   * Helper class to update info schema.
   *
   * @author Jihan Chen
   *
   */
  static class UpdateInfoSchema {

    // private static File schemata = FileUtils.tblRef(InfoSchema.getInfoSchema(),
    // InfoSchema.getSchemata());
    // private static File tables = FileUtils.tblRef(InfoSchema.getInfoSchema(),
    // InfoSchema.getTables());
    // private static File columns = FileUtils.tblRef(InfoSchema.getInfoSchema(),
    // InfoSchema.getColumns());

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
}


