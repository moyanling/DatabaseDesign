package org.mo39.fmbh.databasedesign.model;

import java.util.Collections;
import java.util.List;

public abstract class DataType {

  public abstract Object parse(Object obj);

  private static List<DataType> supportedDataTypeList;

  /**
   * Check whether input string is a supported DataType and convert to the corresponding DataType.
   *
   * @return returns {@code null} if not supported. returns corresponding DataType if supported.
   */
  public static DataType supports(String arg) {
    // TODO
    return null;
  }

  public static List<DataType> getDataTypeList() {
    return supportedDataTypeList;
  }

  public static void setDataTypeList(List<DataType> supportedDataTypeList) {
    DataType.supportedDataTypeList = Collections.unmodifiableList(supportedDataTypeList);
  }

  public static class Primary extends DataType {

    @Override
    public Object parse(Object obj) {
      // TODO Auto-generated method stub
      return null;
    }

  }

  public static class NotNull extends DataType {

    @Override
    public Object parse(Object obj) {
      // TODO Auto-generated method stub
      return null;
    }

  }

}
