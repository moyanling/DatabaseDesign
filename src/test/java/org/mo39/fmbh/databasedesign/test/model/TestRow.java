package org.mo39.fmbh.databasedesign.test.model;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mo39.fmbh.databasedesign.framework.DatabaseDesign;
import org.mo39.fmbh.databasedesign.model.Column;
import org.mo39.fmbh.databasedesign.model.Row;

public class TestRow {

  @Before
  public void before() {
    new DatabaseDesign();
  }

  @Test
  public void testConcatenateByteArrays() {
    byte[] byteArray1 = {1, 2, 3};
    byte[] byteArray2 = {4, 5, 6};
    byteArray1 = ArrayUtils.addAll(byteArray1, byteArray2);
    Assert.assertArrayEquals(new byte[] {1, 2, 3, 4, 5, 6}, byteArray1);
  }

  @Test
  public void testAddRecord() {
    Row row = Row.init(Column.newColumnDefinition("id int, description varchar(20)"));
    row.addRecord("39, Hello world");
    Assert.assertArrayEquals(
        new byte[] {0, 0, 0, 39, 11, 72, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100},
        row.getRecord());
  }

}
