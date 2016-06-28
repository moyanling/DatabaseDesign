package org.mo39.fmbh.databasedesign.model;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.framework.SystemProperties;

import com.google.common.base.Preconditions;

public class DataType {

  private String name;
  private String regx;
  private String description;

  private String javaClass;
  private String parseToByteArray;
  private String parseFromByteBuffer;

  private static List<DataType> supportedDataTypeList;

  /**
   * Check whether input string is a supported DataType and convert to the corresponding DataType.
   *
   * @return returns {@code null} if not supported. returns corresponding DataType if supported.
   */
  public static DataType supports(String arg) {
    Preconditions.checkArgument(arg != null);
    for (DataType type : DataType.supportedDataTypeList) {
      Pattern regx = Pattern.compile(type.regx, Pattern.CASE_INSENSITIVE);
      Matcher matcher = regx.matcher(arg);
      if (matcher.matches()) {
        return type;
      }
    }
    return null;
  }

  /**
   * Take the input arg as a Integer and parse to a four-length byte array.
   *
   * @param arg
   * @return
   */
  public static final byte[] parseIntToByteArray(String arg) {
    return ByteBuffer.allocate(4).putInt(Integer.parseInt(arg)).array();
  }

  /**
   * Parse the byte array back to an int.
   *
   * @param arr
   * @return
   */
  public static final Integer parseIntFromByteBuffer(ByteBuffer bb) {
    return bb.getInt();
  }

  /**
   * Take the input arg as a byte and parse to a 1-length byte array.
   *
   * @param arg
   * @return
   */
  public static final byte[] parseByteToByteArray(String arg) {
    return ByteBuffer.allocate(1).put(Byte.parseByte(arg)).array();
  }

  /**
   * Parse the byte array back to a byte.
   *
   * @param arr
   * @return
   */
  public static final Byte parseByteFromByteBuffer(ByteBuffer bb) {
    return bb.get();
  }

  /**
   * Take the input arg as a varchar and parse to a byte array prepended the length. The length is
   * presented by a byte, ranging from 0-127.
   *
   * @param arg
   * @return
   */
  public static final byte[] parseVarCharToByteArray(String arg) {
    byte[] bytes = arg.getBytes(SystemProperties.getCharset());
    byte len = (byte) bytes.length;
    int capacity = 1 + bytes.length;
    ByteBuffer bb = ByteBuffer.allocate(capacity);
    bb.put(len);
    bb.put(bytes);
    return bb.array();
  }

  /**
   * Parse the byte array back to a String.
   *
   * @param arr
   * @return
   */
  public static final String parseVarCharFromByteBuffer(ByteBuffer bb) {
    byte len = bb.get();
    byte[] s = new byte[len];
    bb.get(s, 0, len);
    return new String(s, SystemProperties.getCharset());
  }

  /**
   * Take the input arg as a long and parse to a byte array;
   *
   * @param arg
   * @return
   */
  public static final byte[] parseLongToByteArray(String arg) {
    return ByteBuffer.allocate(8).putLong(Long.parseLong(arg)).array();
  }

  /**
   * Parse the byte array back to a long.
   *
   * @param arr
   * @return
   */
  public static final long parseLongFromByteBuffer(ByteBuffer bb) {
    return bb.getLong();
  }

  /**
   * Convert from class to corresponding primitive type.
   *
   * @param classType
   * @return
   */
  public static Class<?> converToPrimitiveType(String classType) {
    switch (classType) {
      case "java.lang.Integer":
        return Integer.TYPE;
      case "java.lang.Byte":
        return Byte.TYPE;
      case "java.lang.Short":
        return Short.TYPE;
      case "java.lang.Long":
        return Long.TYPE;
      case "java.lang.Double":
        return Double.TYPE;
      case "java.lang.Float":
        return Float.TYPE;
      default:
        DBExceptions.newError("Not a primitive type. Method not found.");
        return null;
    }
  }

  public static List<DataType> getDataTypeList() {
    return supportedDataTypeList;
  }

  public static void setDataTypeList(List<DataType> supportedDataTypeList) {
    DataType.supportedDataTypeList = Collections.unmodifiableList(supportedDataTypeList);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRegx() {
    return regx;
  }

  public void setRegx(String regx) {
    this.regx = regx;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getJavaClass() {
    return javaClass;
  }

  public void setJavaClass(String javaClass) {
    this.javaClass = javaClass;
  }

  public String getParseFromByteBuffer() {
    return parseFromByteBuffer;
  }

  public void setParseFromByteBuffer(String parseFromByteBuffer) {
    this.parseFromByteBuffer = parseFromByteBuffer;
  }

  public String getParseToByteArray() {
    return parseToByteArray;
  }

  public void setParseToByteArray(String parseToByteArray) {
    this.parseToByteArray = parseToByteArray;
  }

}
