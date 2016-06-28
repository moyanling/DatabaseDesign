package org.mo39.fmbh.databasedesign.model;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

public class DataType {

  private String name;
  private String regx;
  private String description;

  private String javaClass;
  private String parseToBytes;
  private String parseFromBytes;

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

  public String getParseToByte() {
    return parseToBytes;
  }

  public void setParseToByte(String parseToBytes) {
    this.parseToBytes = parseToBytes;
  }

  public String getParseFromByte() {
    return parseFromBytes;
  }

  public void setParseFromByte(String parseFromBytes) {
    this.parseFromBytes = parseFromBytes;
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

}
