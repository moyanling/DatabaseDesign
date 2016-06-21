package org.mo39.fmbh.databasedesign.model;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DataType<T> {

  private String name;
  private String regx;
  private String description;

  public abstract T defaultVlaue();

  @SuppressWarnings("rawtypes")
  private static List<DataType> supportedDataTypeList;

  /**
   * Check whether input string is a supported DataType and convert to the corresponding DataType.
   *
   * @return returns {@code null} if not supported. returns corresponding DataType if supported.
   */
  @SuppressWarnings("rawtypes")
  public static DataType supports(String arg) {
    for (DataType type : DataType.supportedDataTypeList) {
      Pattern regx = Pattern.compile(type.regx, Pattern.CASE_INSENSITIVE);
      Matcher matcher = regx.matcher(arg);
      if (matcher.matches()) {
        return type;
      }
    }
    return null;
  }

  @SuppressWarnings("rawtypes")
  public static List<DataType> getDataTypeList() {
    return supportedDataTypeList;
  }

  @SuppressWarnings("rawtypes")
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

  public static class DbInt extends DataType<Integer> {

    @Override
    public Integer defaultVlaue() {
      return -1;
    }

  }

  public static class DbByte extends DataType<Byte> {

    @Override
    public Byte defaultVlaue() {
      return -1;
    }

  }
  public static class DbVarChar extends DataType<Character[]> {

    @Override
    public Character[] defaultVlaue() {
      return new Character[]{'\0'};
    }

  }

}
