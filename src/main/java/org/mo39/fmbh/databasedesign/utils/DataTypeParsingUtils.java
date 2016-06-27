package org.mo39.fmbh.databasedesign.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.mo39.fmbh.databasedesign.framework.SystemProperties;

/**
 * Utilization class helps to parse DataType.
 *
 * @author Jihan Chen
 *
 */
public abstract class DataTypeParsingUtils {

  /**
   * Take the input arg as a Integer and parse to a four-length byte array.
   *
   * @param arg
   * @return
   */
  public static final byte[] parseIntToBytes(String arg) {
    return ByteBuffer.allocate(4).putInt(Integer.parseInt(arg)).array();
  }

  /**
   * Parse the byte array back to an int.
   *
   * @param arr
   * @return
   */
  public static final Integer parseIntFromBytes(byte[] arr) {
    return ByteBuffer.wrap(arr).getInt();
  }

  /**
   * Take the input arg as a byte and parse to a 1-length byte array.
   *
   * @param arg
   * @return
   */
  public static final byte[] parseByteToBytes(String arg) {
    return ByteBuffer.allocate(1).put(Byte.parseByte(arg)).array();
  }

  /**
   * Parse the byte array back to a byte.
   *
   * @param arr
   * @return
   */
  public static final Byte parseByteFromBytes(byte[] arr) {
    return ByteBuffer.wrap(arr).get();
  }

  /**
   * Take the input arg as a varchar and parse to a byte array prepended the length. The length is
   * presented by a byte, ranging from 0-127.
   *
   * @param arg
   * @return
   */
  public static final byte[] parseVarCharToBytes(String arg) {
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
  public static final String parseVarCharFromBytes(byte[] arr) {
    return new String(Arrays.copyOfRange(arr, 1, arr.length), SystemProperties.getCharset());
  }

  /**
   * Take the input arg as a long and parse to a byte array;
   *
   * @param arg
   * @return
   */
  public static final byte[] parseLongToBytes(String arg) {
    return ByteBuffer.allocate(8).putLong(Long.parseLong(arg)).array();
  }

  /**
   * Parse the byte array back to a long.
   *
   * @param arr
   * @return
   */
  public static final long parseLongFromBytes(byte[] arr) {
    return ByteBuffer.wrap(arr).getLong();
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
        throw new Error("Primitive type not found");
    }
  }


}
