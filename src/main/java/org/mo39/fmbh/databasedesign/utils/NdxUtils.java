package org.mo39.fmbh.databasedesign.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.mo39.fmbh.databasedesign.model.Column;
import org.mo39.fmbh.databasedesign.model.DataType;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

/**
 * Helps to handle ndx file.
 *
 * @author Jihan Chen
 *
 */
class NdxUtils {

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
    File file = IOUtils.ndxRef(schema, table, col.getName());
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
    File file = IOUtils.ndxRef(schema, table, col.getName());
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