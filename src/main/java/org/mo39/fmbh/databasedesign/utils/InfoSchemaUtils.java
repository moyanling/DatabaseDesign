package org.mo39.fmbh.databasedesign.utils;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.mo39.fmbh.databasedesign.framework.InfoSchema;
import org.mo39.fmbh.databasedesign.model.Column;
import org.mo39.fmbh.databasedesign.model.Constraint;
import org.mo39.fmbh.databasedesign.model.Constraint.NoConstraint;
import org.mo39.fmbh.databasedesign.model.Constraint.NotNull;
import org.mo39.fmbh.databasedesign.model.Constraint.PrimaryKey;
import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.DataType;
import org.mo39.fmbh.databasedesign.model.Table;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.ByteSink;
import com.google.common.io.FileWriteMode;
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
   * <p>
   * If the definition is updated, this function need to be updated as well.
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
      schemas.remove(InfoSchema.getInfoSchema());// Remove INFORMATION_SCHEMA from schemas set.
    } catch (IOException e) {
      DBExceptions.newError(e);
    }
    return schemas;
  }

  /**
   * Get tables from TABLES. This function is not relaying on the definition of TABLES table.
   * <p>
   * If the definition is updated, this function need to be updated as well.
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
   * Get columns from COLUMNS. This function is not relaying on the definition of COLUMNS table.
   * <p>
   * If the definition is updated, this function need to be updated as well.
   *
   * @param schema
   * @param table
   * @return {@code List<Column>} a list of {@link Column} objects describing a table.
   */
  public static List<Column> getColumns(String schema, String table) {
    checkArgument(schema != null && table != null);
    List<Column> cols = Lists.newArrayList();
    Column col = null;
    try {
      byte[] fileContent =
          Files.toByteArray(FileUtils.tblRef(InfoSchema.getInfoSchema(), InfoSchema.getColumns()));
      ByteBuffer bb = ByteBuffer.wrap(fileContent);
      while (bb.hasRemaining()) {
        if ((col = byteBufferToColumn(schema, table, bb)) != null) {
          cols.add(col);
        }
      }
    } catch (IOException e) {
      DBExceptions.newError(e);
    }
    /**
     * Currently, the column objects are maintained by an ArrayList. The Column objects read from DB
     * should observe the sequence of their ordinal position. So the Collection#sort() method should
     * have no effect. In case a problem happens, here puts a order check to make sure sort method
     * indeed takes no effect. If the order is changed, a assertion error is thrown, which indicates
     * that a manual fix is necessary.
     * <p>
     * Also, Column class does not provide a equals method, but Assert#assertEquals() still works
     * propertly. This is because the List collection points to its actually elements. So when List
     * temp is created as a new ArrayList with cols as the param, no new Column object is created.
     * List temp and List cols shares the same elements but are maintained in different List.
     */
    List<Column> temp = Lists.newArrayList(cols);
    Collections.sort(cols);
    Assert.assertEquals(temp, cols);
    return cols;
  }

  /**
   * Parse a {@link Column} object to byte array. This function is not relaying on the definition of
   * COLUMNS table.
   * <p>
   * If the definition is updated, this function need to be updated as well.
   *
   * @param schema
   * @param table
   * @param col
   * @return
   */
  private static byte[] columnToBytes(String schema, String table, Column col) {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      out.write(DataType.parseVarCharToByteArray(schema));
      out.write(DataType.parseVarCharToByteArray(table));
      out.write(DataType.parseVarCharToByteArray(col.getName()));
      out.write(DataType.parseByteToByteArray(String.valueOf(col.getOrdinalPosi())));
      out.write(DataType.parseVarCharToByteArray(col.getDataType().getArg()));
      // parse the constraint
      Constraint con = col.getConstraint();
      if (con instanceof NotNull) {
        out.write(DataType.parseVarCharToByteArray("YES"));
        out.write(DataType.parseVarCharToByteArray("NULL"));
      } else if (con instanceof PrimaryKey) {
        out.write(DataType.parseVarCharToByteArray("YES"));
        out.write(DataType.parseVarCharToByteArray("YES"));
      } else if (con instanceof NoConstraint) {
        out.write(DataType.parseVarCharToByteArray("NULL"));
        out.write(DataType.parseVarCharToByteArray("NULL"));
      }
      // ----------------------
      return out.toByteArray();
    } catch (IOException e) {
      DBExceptions.newError(e);
      return null;
    }
  }

  /**
   * Read in one Column byte array from COLUMNS and convert to a Column Object. This function is not
   * relaying on the definition of COLUMNS table.
   * <p>
   * If the definition is updated, this function need to be updated as well.
   * <p>
   * If a column is the primary key, the not null attribute for this column is "YES" as well.
   * </p>
   *
   * @param schema
   * @param table
   * @param bb
   * @return {@code null} if the schema and table name are not matched, else a new {@link Column}
   *         object is returned.
   */
  private static Column byteBufferToColumn(String schema, String table, ByteBuffer bb) {
    Column col = null;
    // Read in one Column byte array.
    String schemaName = DataType.parseVarCharFromByteBuffer(bb);
    String tableName = DataType.parseVarCharFromByteBuffer(bb);
    String columnName = DataType.parseVarCharFromByteBuffer(bb);
    int ordinalPosi = DataType.parseByteFromByteBuffer(bb);
    String dataTypeArg = DataType.parseVarCharFromByteBuffer(bb);
    String notNullArg = DataType.parseVarCharFromByteBuffer(bb);
    String primaryArg = DataType.parseVarCharFromByteBuffer(bb);
    // ----------------------
    if (schemaName.equals(schema) && tableName.equals(table)) {
      // parse the constraint
      Constraint con = null;
      if (notNullArg == null && primaryArg == null) {
        con = Constraint.supports("");
      } else if (notNullArg.equals("YES") && primaryArg == null) {
        con = Constraint.supports("NOT NULL");
      } else if (notNullArg.equals("YES") && primaryArg.equals("YES")) {
        con = Constraint.supports("PRIMARY KEY");
      } else {
        DBExceptions.newError("No constraint is found");
      }
      // ----------------------
      col = new Column(columnName, DataType.supports(dataTypeArg), con, ordinalPosi);
    }
    return col;
  }



  /**
   * Helper class to update info schema.
   *
   * @author Jihan Chen
   *
   */
  static class UpdateInfoSchema {

    private static File schemata =
        FileUtils.tblRef(InfoSchema.getInfoSchema(), InfoSchema.getSchemata());
    private static File tables =
        FileUtils.tblRef(InfoSchema.getInfoSchema(), InfoSchema.getTables());
    private static File columns =
        FileUtils.tblRef(InfoSchema.getInfoSchema(), InfoSchema.getColumns());

    /**
     * Update information schema when new schema is created. This function appends the new schema
     * name to the SCHEMATA table.
     *
     * @param schema
     */
    public static void atCreatingSchema(String schema) {
      ByteSink out = Files.asByteSink(schemata, FileWriteMode.APPEND);
      try {
        out.write(DataType.parseVarCharToByteArray(schema));
      } catch (IOException e) {
        DBExceptions.newError(e);
      }
    }

    /**
     * Update information schema when new table is created. This function first appends the byte
     * presentation of Column definitions to COLUMNS table then overwrite TABLES. The new content
     * will contain the information of the newly created table with 0 row number and update the row
     * number of INFORMATION_SCHEMA.COLUMNS adding the number of columns in this newly created
     * table.
     *
     * @param schema
     * @param table
     * @param cols
     */
    public static void atCreatingTable(String schema, String table, List<Column> cols) {
      ByteSink columnsOut = Files.asByteSink(columns, FileWriteMode.APPEND);
      try {
        // Write to COLUMNS
        for (Column col : cols) {
          columnsOut.write(InfoSchemaUtils.columnToBytes(schema, table, col));
        }
        // rewrite the content of TABLES
        byte[] tablesContent = Files.toByteArray(tables);
        List<InfoTable> infoTableList = InfoTable.getInfoTableList(ByteBuffer.wrap(tablesContent));
        // Update the number of rows in INFORMATION_SCHEMA.COLUMNS
        for (InfoTable it : infoTableList) {
          if (schema.equals(InfoSchema.getInfoSchema()) && table.equals(InfoSchema.getTables())) {
            it.rows += cols.size();
          }
        }
        // Append the newly created table with 0 row number.
        infoTableList.add(new InfoTable(schema, table, 0));
        // ----------------------
        Files.write(InfoTable.listToBytes(infoTableList), tables);
      } catch (IOException e) {
        DBExceptions.newError(e);
      }
    }

    /**
     * Update information schema when table is dropped. This function removes the table from TABLES,
     * and removes its columns from COLUMS.
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
      // try to use filter
    }

    /**
     * Update information schema when new records are appended to BD
     * 
     * @param schema
     * @param table
     * @param i
     */
    public static void atAppendingNewRecord(String schema, String table, Table t) {
      try {
        List<InfoTable> l = InfoTable.getInfoTableList(ByteBuffer.wrap(Files.toByteArray(tables)));
        for (InfoTable infoTable : l) {
          if (infoTable.schema.equals(schema) && infoTable.table.equals(table)) {
            infoTable.rows += t.size();
          }
        }
        Files.write(InfoTable.listToBytes(l), tables);
      } catch (IOException e) {
        DBExceptions.newError(e);
      }
    }

    private static class InfoTable {
      public String schema;
      public String table;
      public int rows;

      public InfoTable(String schema, String table, int rows) {
        this.schema = schema;
        this.table = table;
        this.rows = rows;
      }

      private static InfoTable parseFromByteBuffer(ByteBuffer bb) {
        String schema = DataType.parseVarCharFromByteBuffer(bb);
        String table = DataType.parseVarCharFromByteBuffer(bb);
        int rows = DataType.parseIntFromByteBuffer(bb);
        return new InfoTable(schema, table, rows);
      }

      private byte[] toBytes() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
          out.write(DataType.parseVarCharToByteArray(schema));
          out.write(DataType.parseVarCharToByteArray(table));
          out.write(DataType.parseIntToByteArray(String.valueOf(rows)));
          return out.toByteArray();
        } catch (IOException e) {
          DBExceptions.newError(e);
        }
        return null;
      }

      public static List<InfoTable> getInfoTableList(ByteBuffer bb) {
        List<InfoTable> infoTableList = Lists.newArrayList();
        while (bb.hasRemaining()) {
          infoTableList.add(parseFromByteBuffer(bb));
        }
        return infoTableList;
      }

      public static byte[] listToBytes(List<InfoTable> list) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
          for (InfoTable it : list) {
            out.write(it.toBytes());
          }
          return out.toByteArray();
        } catch (IOException e) {
          DBExceptions.newError(e);
        }
        return null;

      }

    }
  }
}


