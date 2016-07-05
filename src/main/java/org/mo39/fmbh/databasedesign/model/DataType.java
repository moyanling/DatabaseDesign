package org.mo39.fmbh.databasedesign.model;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.framework.SystemProperties;

import com.google.common.base.Preconditions;

public class DataType {

  private String arg;

  private String name;
  private String regx;
  private String description;

  private String javaClass;
  private String parseToByteArray;
  private String parseFromByteBuffer;

  private static List<DataType> supportedDataTypeList;

  private static Pattern nullValue = Pattern.compile("NULL", Pattern.CASE_INSENSITIVE);

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
        DataType toRet = type.clone();
        toRet.arg = arg;
        return toRet;
      }
    }
    return null;
  }

  public DataType clone() {
    DataType toRet = new DataType();
    toRet.arg = arg;
    toRet.name = name;
    toRet.regx = regx;

    toRet.description = description;
    toRet.javaClass = javaClass;

    toRet.parseToByteArray = parseToByteArray;
    toRet.parseFromByteBuffer = parseFromByteBuffer;
    return toRet;
  }

  /**
   * Check some constraints implied by a datatype. It now checks the length of the varchar only.
   *
   * @return
   */
  public static boolean checkDataType(Column col, String value) {
    Pattern p = Pattern.compile("NULL", Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(value);
    if (m.matches()) {
      return true;
    }
    DataType dt = col.getDataType();
    switch (dt.name) {
      case "VARCHAR":
        Matcher matcher = Pattern.compile(dt.regx, Pattern.CASE_INSENSITIVE).matcher(dt.arg);
        matcher.matches();
        if (Integer.parseInt(matcher.group(1)) < value.length()) {
          return false;
        }
      default:
        return true;
    }
  }

  /**
   * Take the input arg as a Integer and parse to a four-length byte array.
   *
   * @param arg
   * @return
   */
  public static byte[] parseIntToByteArray(String arg) {
    Preconditions.checkArgument(arg != null);
    Matcher m = nullValue.matcher(arg);
    if (m.matches()) {
      return ByteBuffer.allocate(4).putInt(Integer.MIN_VALUE).array();
    }
    return ByteBuffer.allocate(4).putInt(Integer.parseInt(arg)).array();
  }

  /**
   * Parse the byte array back to an int.
   *
   * @param arr
   * @return
   */
  public static Integer parseIntFromByteBuffer(ByteBuffer bb) {
    int i = bb.getInt();
    return i == Integer.MIN_VALUE ? null : i;
  }

  /**
   * Take the input arg as a byte and parse to a 1-length byte array.
   *
   * @param arg
   * @return
   */
  public static byte[] parseByteToByteArray(String arg) {
    Preconditions.checkArgument(arg != null);
    Matcher m = nullValue.matcher(arg);
    if (m.matches()) {
      return ByteBuffer.allocate(1).put(Byte.MIN_VALUE).array();
    }
    return ByteBuffer.allocate(1).put(Byte.parseByte(arg)).array();
  }

  /**
   * Parse the byte array back to a byte.
   *
   * @param arr
   * @return
   */
  public static Byte parseByteFromByteBuffer(ByteBuffer bb) {
    byte b = bb.get();
    return b == Byte.MIN_VALUE ? null : b;
  }

  /**
   * Take the input arg as a varchar and parse to a byte array prepended the length. The length is
   * presented by a byte, ranging from 0-127.
   *
   * @param arg
   * @return
   */
  public static byte[] parseVarCharToByteArray(String arg) {
    Preconditions.checkArgument(arg != null);
    Matcher m = nullValue.matcher(arg);
    if (m.matches()) {
      return new byte[] {0};
    }
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
  public static String parseVarCharFromByteBuffer(ByteBuffer bb) {
    byte len = bb.get();
    if (len == 0) {
      return null;
    }
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
  public static byte[] parseLongToByteArray(String arg) {
    Preconditions.checkArgument(arg != null);
    Matcher m = nullValue.matcher(arg);
    if (m.matches()) {
      return ByteBuffer.allocate(8).putLong(Long.MIN_VALUE).array();
    }
    return ByteBuffer.allocate(8).putLong(Long.parseLong(arg)).array();
  }
  
  /**
   * Skip reading the next varchar.
   * 
   * @param bb
   */
  public static void skipVarChar(ByteBuffer bb) {
    byte len = bb.get();
    bb.position(bb.position() + len);
  }

  /**
   * Parse the byte array back to a long.
   *
   * @param arr
   * @return
   */
  public static long parseLongFromByteBuffer(ByteBuffer bb) {
    long l = bb.getLong();
    return l == Long.MIN_VALUE ? null : l;
  }

  public static List<DataType> getDataTypeList() {
    return supportedDataTypeList;
  }

  public static void setDataTypeList(List<DataType> supportedDataTypeList) {
    DataType.supportedDataTypeList = Collections.unmodifiableList(supportedDataTypeList);
  }

  public String getArg() {
    return arg;
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
