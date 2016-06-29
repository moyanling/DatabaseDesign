package org.mo39.fmbh.databasedesign.test.model;

import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mo39.fmbh.databasedesign.framework.DatabaseDesign;

import com.google.common.collect.Lists;

public class TestTable {

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
//    Table t = Table.init(Column.newColumnDefinition("id int, description varchar(20)"));
//    t.addRecord("39, Hello world");
//    Assert.assertArrayEquals(
//        new byte[] {0, 0, 0, 39, 11, 72, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100},
//        t.getRecord());
  }

  @Test
  public void testRemoveRecordFromList() {
    ArrayList<String> x = Lists.newArrayList("0", "1", "2", "3", "4", "5");
    for (int i = 0; i < x.size(); i++) {
      Assert.assertEquals(new Integer(i).toString(), x.remove(0));
    }
  }

}
