package org.mo39.fmbh.databasedesign.utils;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.mo39.fmbh.databasedesign.framework.DatabaseDesign;
import org.mo39.fmbh.databasedesign.framework.InfoSchema;
import org.mo39.fmbh.databasedesign.model.Column;
import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.DBExceptions.ColumnNameNotFoundException;
import org.mo39.fmbh.databasedesign.model.DataType;
import org.mo39.fmbh.databasedesign.model.Table;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSink;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

public abstract class TblUtils {

  /**
   * Parse the String form record into byte array and append the bytes to the end of the tbl file.
   *
   * @param t
   * @param schema
   * @param table
   * @throws IOException
   */
  public static void appendRecordsToDB(Table t, String schema, String table) throws IOException {
    int rows = t.size();
    File tbl = FileUtils.tblRef(schema, table);
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
         * corrsponding ndx file for this column will be updated.
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
              (int) FileUtils.tblRef(schema, table).length());
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
    byte[] fileContent = Files.toByteArray(FileUtils.tblRef(schema, table));
    ByteBuffer bb = ByteBuffer.wrap(fileContent);
    List<Object> toRet = Lists.newArrayList();
    List<Column> cols = FileUtils.getColumnList(schema, table);
    // No columnName and value specified
    if (whereClause.equals("")) {
      while (bb.hasRemaining()) {
        toRet.add(BeanUtils.parse(beanClass, cols, bb));
      }
      return toRet;
    }
    // Find record according to index file
    try {
      // Extract column name and value
      Pattern p = Pattern.compile("WHERE(.*)=(.*)", Pattern.CASE_INSENSITIVE);
      Matcher m = p.matcher(whereClause);
      m.matches();
      String columnName = m.group(1).trim();
      String value = m.group(2).trim();
      // Get Column according to the columnName
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
      // ----------------------
      List<NdxUtils.Ndx> ndxList = NdxUtils.getNdxList(schema, table, column);
      NdxUtils.Ndx ndx = NdxUtils.findNdx(column, value, ndxList);
      List<Integer> positions = ndx.positions;
      for (Integer posi : positions) {
        bb.position(posi);
        toRet.add(BeanUtils.parse(beanClass, cols, bb));
      }
    } catch (DBExceptions e) {
      throw e;
    } catch (Exception e) {
      DBExceptions.newError(e);
    }
    return toRet;
  }

  /**
   * Check if there's no duplicate value for a primary key.
   *
   * @param schema
   * @param table
   * @param col
   * @param value
   * @return
   */
  public static boolean checkPrimaryKey(String schema, String table, Column col, String value) {
    try {
      List<NdxUtils.Ndx> ndxList = NdxUtils.getNdxList(schema, table, col);
      return NdxUtils.findNdx(col, value, ndxList) == null;
    } catch (Exception e) {
      DBExceptions.newError(e);
      return false;
    }
  }



  private static class NdxUtils {

    /**
     * When a column of the record is written to DB, call this method to upate the corresponding ndx
     * file for this column.
     *
     * @param schema
     * @param table
     * @param col
     * @param value
     * @param position
     * @throws Exception
     */
    public static void updateIndexAtAppendingColumn(String schema, String table, Column col,
        String value, int position) throws Exception {
      File file = FileUtils.ndxRef(schema, table, col.getName());
      List<Ndx> ndxList = getNdxList(schema, table, col);
      Ndx ndx = findNdx(col, value, ndxList);
      // ----------------------
      if (ndx == null) {
        // If no position is found, append the new one to the ndx list.
        Ndx newNdx = new Ndx(col, value, position);
        ndxList.add(newNdx);
      } else {
        // If a position can be found, need to update the value and add new position
        ndx.num += 1;
        ndx.positions.add(position);
      }
      // Convert the whole ndx list to byte array
      byte[] ndxBytes;
      byte[] newContent = new byte[0];
      for (Ndx n : ndxList) {
        ndxBytes = n.parseToBytes();
        newContent = ArrayUtils.addAll(newContent, ndxBytes);
      }
      Files.write(newContent, file);
    }

    /**
     * Convert certain ndx file into a List containing Ndx objects.
     *
     * @param schema
     * @param table
     * @param col
     * @return
     * @throws Exception
     */
    public static List<Ndx> getNdxList(String schema, String table, Column col) throws Exception {
      // Read in all bytes
      File file = FileUtils.ndxRef(schema, table, col.getName());
      byte[] fileContent = Files.toByteArray(file);
      // Change to Ndx Object
      List<Ndx> ndxList = Lists.newArrayList();
      ByteBuffer bb = ByteBuffer.wrap(fileContent);
      while (bb.hasRemaining()) {
        ndxList.add(Ndx.parseFromBytes(bb, col));
      }
      return ndxList;
    }

    /**
     *
     *
     * @param col
     * @param value
     * @param ndxList
     * @return
     * @throws Exception
     */
    public static Ndx findNdx(Column col, String value, List<Ndx> ndxList) throws Exception {
      byte[] byteArray = (byte[]) DataType.class
          .getMethod(col.getDataType().getParseToByteArray(), String.class).invoke(null, value);
      Object dataTypeValue =
          DataType.class.getMethod(col.getDataType().getParseFromByteBuffer(), ByteBuffer.class)
              .invoke(null, ByteBuffer.wrap(byteArray));
      for (Ndx ndx : ndxList) {
        if (ndx.value == null ? dataTypeValue == null : ndx.value.equals(dataTypeValue)) {
          return ndx;
        }
      }
      return null;
    }

    /**
     * Class presentation for the value indexes(positions) pairs in ndx file for a certain column.
     * <p>
     * Since NdxUtils is a private class, this Ndx class is not accessable from outside, so it's
     * variable member modifiers are public for convenience. Take care of that when using this
     * class.
     *
     * @author Jihan Chen
     *
     */
    public static class Ndx {

      public Object value;
      public int num;
      public List<Integer> positions;

      public Column col;

      public Ndx(Object value, int num, List<Integer> positions, Column col) {
        this.value = value;
        this.positions = positions;
        this.col = col;
        this.num = num;
      }

      public Ndx(Column col, String value, int posi) throws Exception {
        byte[] byteArray = (byte[]) DataType.class
            .getMethod(col.getDataType().getParseToByteArray(), String.class).invoke(null, value);
        Object dataTypeValue =
            DataType.class.getMethod(col.getDataType().getParseFromByteBuffer(), ByteBuffer.class)
                .invoke(null, ByteBuffer.wrap(byteArray));
        this.value = dataTypeValue;
        num = 1;
        positions = Lists.newArrayList(posi);
        this.col = col;
      }

      /**
       * parse the byte array to create a Ndx Object
       *
       * @param bb
       * @param col
       * @return
       * @throws Exception
       */
      public static Ndx parseFromBytes(ByteBuffer bb, Column col) throws Exception {
        Method method =
            DataType.class.getMethod(col.getDataType().getParseFromByteBuffer(), ByteBuffer.class);
        Object value = method.invoke(null, bb);
        int num = DataType.parseIntFromByteBuffer(bb);
        List<Integer> positions = Lists.newArrayList();
        for (int i = 0; i < num; i++) {
          positions.add(DataType.parseIntFromByteBuffer(bb));
        }
        return new Ndx(value, num, positions, col);
      }

      /**
       * Parse the Ndx object to a byte array.
       *
       * @return
       * @throws Exception
       */
      public byte[] parseToBytes() throws Exception {
        byte[] posiBytes;
        byte[] result = new byte[0];
        Method method =
            DataType.class.getMethod(col.getDataType().getParseToByteArray(), String.class);
        if (value == null) {
          value = "NULL";
        }
        byte[] valueBytes = (byte[]) method.invoke(null, value.toString());
        result = ArrayUtils.addAll(result, valueBytes);
        byte[] numBytes = DataType.parseIntToByteArray(String.valueOf(num));
        result = ArrayUtils.addAll(result, numBytes);
        for (Integer posi : positions) {
          posiBytes = DataType.parseIntToByteArray(String.valueOf(posi));
          result = ArrayUtils.addAll(result, posiBytes);
        }
        return result;
      }
    }

  }

  public static void main(String[] args) {
    new DatabaseDesign();
  }

}


