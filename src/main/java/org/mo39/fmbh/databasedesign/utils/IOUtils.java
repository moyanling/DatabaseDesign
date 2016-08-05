package org.mo39.fmbh.databasedesign.utils;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.mo39.fmbh.databasedesign.model.Column;
import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.DBExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.model.DBExceptions.ColumnNameNotFoundException;
import org.mo39.fmbh.databasedesign.model.DataType;
import org.mo39.fmbh.databasedesign.model.InfoSchema;
import org.mo39.fmbh.databasedesign.model.Table;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteSink;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

/**
 * IO utilization class. Provides methods to read and write to database.
 *
 * @author Jihan Chen
 *
 */
public abstract class IOUtils {

  /**
   * Return a tbl file reference according to schema and table name.
   *
   * @param schema
   * @param table
   * @return
   */
  public static final File tblRef(String schema, String table) {
    checkArgument(schema != null && table != null);
    return Paths.get(InfoSchema.getArchiveRoot(), schema, table + ".tbl").toFile();
  }

  /**
   * Return a ndx file reference according to schema, table and column name.
   *
   * @param schema
   * @param table
   * @param column
   * @return
   */
  public static final File ndxRef(String schema, String table, String column) {
    checkArgument(schema != null && table != null && column != null);
    return Paths.get(InfoSchema.getArchiveRoot(), schema, table + "." + column + ".ndx").toFile();
  }

  /**
   * Select records from DB and form the record into a bean class using {@link BeanUtils}.
   *
   * @param schema
   * @param table
   * @param whereClause
   * @param beanClass
   * @return
   * @throws DBExceptions
   * @throws IOException
   */
  public static List<Object> selectFromDB(String schema, String table, Class<?> beanClass,
      String whereClause) throws DBExceptions, IOException {
    checkArgument(schema != null && table != null && beanClass != null);
    // ----------------------
    byte[] fileContent = Files.toByteArray(tblRef(schema, table));
    ByteBuffer bb = ByteBuffer.wrap(fileContent);
    List<Object> toRet = Lists.newArrayList();
    List<Column> cols = InfoSchemaUtils.getColumns(schema, table);
    // No columnName and value specified
    if (whereClause.equals("")) {
      while (bb.hasRemaining()) {
        Object obj = BeanUtils.parse(beanClass, cols, bb);
        if (obj != null) {
          toRet.add(obj);
        }
      }
      return toRet;
    }
    // ColumnName and value specified
    try {
      List<Integer> positions = locatePositions(schema, table, whereClause, cols);
      for (Integer posi : positions) {
        bb.position(posi);
        Object obj = BeanUtils.parse(beanClass, cols, bb);
        if (obj != null) {
          toRet.add(obj);
        }
      }
    } catch (DBExceptions e) {
      throw e;
    } catch (Exception e) {
      DBExceptions.newError(e);
    }
    return toRet;
  }


  /**
   * Clear all content and append left records to DB.
   *
   * @param schema
   * @param table
   * @param where
   * @throws DBExceptions
   */
  public static void deleteRecord(String schema, String table, String where) throws DBExceptions {
    try {
      List<Column> cols = InfoSchemaUtils.getColumns(schema, table);
      if (locatePositions(schema, table, where, cols).size() == 0) {
        clearAllRecords(schema, table);
        return;
      }
      Table t = Table.valueOf(schema, table);
      Map<String, String> whereMap = parseWhere(where);
      Column col = findColByName(cols, whereMap.get("name"));
      List<String> filteredRecords = t.getRecords().stream()
          .filter(i -> hasValueAtIndex(i, whereMap.get("value"), cols.indexOf(col)))
          .collect(Collectors.toList());
      t.setRecords(filteredRecords);
      clearAllRecords(schema, table);
      t.writeToDB();
    } catch (DBExceptions e) {
      throw e;
    } catch (Exception e) {
      DBExceptions.newError(e);
    }
  }

  /**
   * Set the value of a column name.
   *
   * @param schema
   * @param table
   * @param columnName
   * @param value
   * @param where
   * @throws DBExceptions
   */
  public static void updateRecord(String schema, String table, String columnName, String value,
      String where) throws DBExceptions {
    try {
      List<Column> cols = InfoSchemaUtils.getColumns(schema, table);
      Table t = Table.valueOf(schema, table);

      // No columnName and value specified
      if (!where.equals("")) {
        //TODO do filter
      }


      Map<String, String> whereMap = parseWhere(where);
      Column col = findColByName(cols, whereMap.get("name"));
      String record;
      int indexOfCol = cols.indexOf(col);
      for (int i = 0; i < t.size(); i++) {
        record = t.getRecords().get(i);
        if (hasValueAtIndex(record, whereMap.get("value"), indexOfCol)) {
          String[] valueArr = record.split(",");
          valueArr[i] = value;
          t.getRecords().set(i, Joiner.on(",").join(valueArr));
        }
      }
      clearAllRecords(schema, table);
      t.writeToDB();
    } catch (DBExceptions e) {
      throw e;
    } catch (Exception e) {
      DBExceptions.newError(e);
    }
  }



  /**
   * Rewrite a file using the new byte array. It aims at modifying the content of a file.
   *
   * <p>
   * Use {@link com.google.common.io.Files.write()} instead, which helps to overwrite a file using a
   * byte array and shares the same params as well.
   *
   * @param file
   * @param newContent
   * @throws IOException
   */
  @Deprecated
  public static void rewrite(File file, byte[] newContent) throws IOException {
    checkArgument(file != null && newContent != null);
    if (!file.isFile()) {
      throw new Error("Parameter is not an existing file");
    }
    if (!file.delete()) {
      throw new Error("Could not delete file");
    }
    File tempFile = new File(file.getAbsolutePath() + ".tmp");
    tempFile.createNewFile();
    java.nio.file.Files.write(tempFile.toPath(), newContent, StandardOpenOption.APPEND);
    if (!tempFile.renameTo(file)) {
      throw new Error("Could not rename file");
    }
  }

  /**
   * Create a new schema i. e.a folder to hold tables.**
   *
   * @param schema
   * @throws IOException
   */
  public static boolean createSchema(String schema) throws IOException {
    if (Paths.get(InfoSchema.getArchiveRoot(), schema).toFile().mkdirs()) {
      InfoSchemaUtils.UpdateInfoSchema.atCreatingSchema(schema);
      return true;
    }
    return false;
  }

  /**
   * This function will take schema as a folder and table as the tbl file name. Unless the schema is
   * information_schema. Related ndx files are created at the same time.
   *
   * @param schema
   * @param table
   * @throws IOException
   */
  public static boolean createtblFile(String schema, String table, List<Column> columns)
      throws IOException {
    if (!tblRef(schema, table).createNewFile()) {
      return false;
    }
    for (Column col : columns) {
      if (!ndxRef(schema, table, col.getName()).createNewFile()) {
        return false;
      }
    }
    InfoSchemaUtils.UpdateInfoSchema.atCreatingTable(schema, table, columns);
    return true;
  }

  /**
   * Delete table in the archive
   *
   * @param schemaName
   * @param tableName
   * @return true if delete successfully else false.
   */
  public static boolean deleteTable(String schema, String table) {
    // Delete ndx files
    File[] files = Paths.get(InfoSchema.getArchiveRoot(), schema).toFile().listFiles();
    for (File f : files) {
      if (f.getName().matches(table + "\\..*?\\.ndx")) {
        if (!f.delete()) {
          return false;
        }
      }
    }
    // Delete tbl file.
    if (tblRef(schema, table).delete()) {
      InfoSchemaUtils.UpdateInfoSchema.atDroppingTable(schema, table);
      return true;
    }
    return false;
  }

  /**
   * Delete a schema in the archive.
   *
   * @param schemaName
   * @return true if delete successfully else false.
   */
  public static boolean deleteSchema(String schema) {
    checkArgument(schema != null);
    File[] files = Paths.get(InfoSchema.getArchiveRoot(), schema).toFile().listFiles();
    for (File f : files) {
      if (!f.delete()) {
        return false;
      }
    }
    if (Paths.get(InfoSchema.getArchiveRoot(), schema).toFile().delete()) {
      InfoSchemaUtils.UpdateInfoSchema.atDeletingSchema(schema);
      return true;
    }
    return false;
  }

  /**
   * Parse the String form record into byte array and append the bytes to the end of the tbl file.
   *
   * @param t
   * @param schema
   * @param table
   * @throws IOException
   */
  public static void appendRecordsToDB(Table t) throws IOException {
    int rows = t.size();
    String schema = t.getSchema();
    String table = t.getTable();
    File tbl = tblRef(schema, table);
    ByteSink out = Files.asByteSink(tbl, FileWriteMode.APPEND);
    ByteArrayOutputStream byteMaker = new ByteArrayOutputStream();
    // Reusable references
    Column col;
    String value;
    String[] valueArray;
    // ----------------------
    try {
      for (String record : t) {
        valueArray = record.split(",");
        /**
         * When a new record is appended to DB, it is separated into several value. Each value
         * matches a Column object. When each value is parsed to byte array and written to DB, the
         * corresponding ndx file for this column will be updated.
         * <p>
         * The Column should be updated before write a whole record to the tbl file.
         */
        for (int i = 0; i < valueArray.length; i++) {
          value = valueArray[i].trim();
          col = t.getColumns().get(i);
          // parse the value for this column and write the value to DB
          Method method =
              DataType.class.getMethod(col.getDataType().getParseToByteArray(), String.class);
          byteMaker.write((byte[]) method.invoke(null, value));
          // ----------------------
          NdxUtils.updateIndexAtAppendingColumn(schema, table, col, value,
              (int) tblRef(schema, table).length());
        }
        out.write(byteMaker.toByteArray());
        // Clear the old byte array
        byteMaker.reset();
      }
      if (!schema.equals(InfoSchema.getInfoSchema())) {
        InfoSchemaUtils.UpdateInfoSchema.atAppendingRecords(schema, table, rows);
      }
    } catch (Exception e) {
      DBExceptions.newError(e);
    }
  }

  /**
   * Helper method that returns the position according to where clause. <br>
   * If no match is found, return an empty list other than null.
   *
   *
   * @param schema
   * @param table
   * @param where
   * @return
   * @throws Exception
   */
  private static List<Integer> locatePositions(String schema, String table, String where,
      List<Column> cols) throws Exception {
    Preconditions.checkArgument(where != "");
    List<Integer> toRet = Lists.newArrayList();
    // Extract column name and value
    Map<String, String> whereMap = parseWhere(where);
    // Get Column according to the columnName
    Column column = findColByName(cols, whereMap.get("name"));
    // ----------------------
    List<NdxUtils.Ndx> ndxList = NdxUtils.getNdxList(schema, table, column);
    NdxUtils.Ndx ndx = NdxUtils.findNdx(column, whereMap.get("value"), ndxList);
    if (ndx != null) {
      toRet = ndx.positions;
    }
    return toRet;
  }

  private static Column findColByName(List<Column> cols, String columnName)
      throws ColumnNameNotFoundException {
    Column column = null;
    for (Column col : cols) {
      if (col.getName().equalsIgnoreCase(columnName)) {
        column = col;
        break;
      }
    }
    if (column == null) {
      throw new ColumnNameNotFoundException("'" + columnName + "' is not found.");
    }
    return column;
  }

  private static Map<String, String> parseWhere(String where) throws BadUsageException {
    Map<String, String> toRet = Maps.newHashMap();
    Matcher m = Pattern.compile("WHERE(.*)=(.*)", Pattern.CASE_INSENSITIVE).matcher(where);
    DbChecker.checkSyntax(m);
    toRet.put("name", m.group(1).trim());
    toRet.put("value", m.group(2).trim());
    return toRet;
  }

  /**
   * Iterate all files in table folder, clear their content.<br>
   * Update infoSchema, set the row number back to zero.
   *
   * @param schema
   * @param table
   */
  private static void clearAllRecords(String schema, String table) {
    try {
      for (File file : Paths.get(InfoSchema.getArchiveRoot(), schema).toFile().listFiles()) {
        if (file.getName().startsWith(table + ".")) {
          Files.write(new byte[] {}, file);
        }
      }
    } catch (IOException e) {
      DBExceptions.newError(e);
    }
    InfoSchemaUtils.UpdateInfoSchema.atClearRecords(schema, table);
  }

  private static boolean hasValueAtIndex(String values, String v, int index) {
    String[] vs = values.split(",");
    return vs[index] == v;
  }

}


