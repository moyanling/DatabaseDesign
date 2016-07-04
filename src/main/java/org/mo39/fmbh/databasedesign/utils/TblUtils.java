package org.mo39.fmbh.databasedesign.utils;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.file.Files;
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

public abstract class TblUtils {

  public static void appendRecordsToDB(Table t, String schema, String table) throws IOException {
    File tbl = FileUtils.tblRef(schema, table);
    OutputStream out = Files.newOutputStream(tbl.toPath(), StandardOpenOption.APPEND);
    // Reusable references
    Column col;
    String value;
    String[] valueArray;
    byte[] bytes;
    byte[] result = new byte[0];
    // ----------------------
    try {
      for (String record : t) {
        valueArray = record.split(",");
        for (int i = 0; i < valueArray.length; i++) {
          value = valueArray[i].trim();
          col = t.getColumns().get(i);
          // ----------------------
          Method method =
              DataType.class.getMethod(col.getDataType().getParseToByteArray(), String.class);
          bytes = (byte[]) method.invoke(null, value);
          result = ArrayUtils.addAll(result, bytes);
          // ----------------------
          NdxUtils.updateIndexAtAppendingRecord(schema, table, col, value,
              (int) FileUtils.tblRef(schema, table).length());
        }
        out.write(result);
        result = new byte[0];// Clear the result byte array for next loop
      }
    } catch (Exception e) {
      DBExceptions.newError(e);
    }
    // Update INFORMATION_SCHEMA if the current schema is not INFORMATION_SCHEMA.
    if (!schema.equals(InfoSchema.getInfoSchema())) {
      // TODO records inserted. need to update information schema.
    }
  }

  /**
   * Rewrite a file using the new byte array. It aims at modifying the content of a file.
   * 
   * @param file
   * @param newContent
   * @throws IOException
   */
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
    Files.write(tempFile.toPath(), newContent, StandardOpenOption.APPEND);
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
    byte[] fileContent = Files.readAllBytes(FileUtils.tblRef(schema, table).toPath());
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
      String columnName = m.group(1);
      String value = m.group(2);
      // Get Column according to the columnName
      Column column = null;
      for (Column col : cols) {
        if (col.getName().equals(columnName)) {
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
    return null;
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
     * Update the ndx file when new record is appended to DB.
     * 
     * @param schema
     * @param table
     * @param col
     * @param value
     * @param position
     * @throws Exception
     */
    public static void updateIndexAtAppendingRecord(String schema, String table, Column col,
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
      rewrite(file, newContent);
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
      byte[] fileContent = Files.readAllBytes(file.toPath());
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
        if (ndx.value == null ? value == dataTypeValue : value.equals(dataTypeValue)) {
          return ndx;
        }
      }
      return null;
    }

    /**
     * Class presentation for the value indexes(positions) in response to it's column.
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
        this.num = 1;
        this.positions = Lists.newArrayList(posi);
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
    System.out
        .println(FileUtils.tblRef(InfoSchema.getInfoSchema(), InfoSchema.getSchemata()).length());
  }

}


