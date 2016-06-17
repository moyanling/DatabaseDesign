package org.mo39.fmbh.databasedesign.model;

import java.util.Collections;
import java.util.List;

public class DataType {

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

}
