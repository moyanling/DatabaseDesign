package org.mo39.fmbh.databasedesign.test.util;

import org.junit.Before;
import org.junit.Test;
import org.mo39.fmbh.databasedesign.framework.DatabaseDesign;
import org.mo39.fmbh.databasedesign.model.Column;
import org.mo39.fmbh.databasedesign.utils.FileUtils;

public class TestFileUtils {

  @Before
  public void before() {
    new DatabaseDesign();
  }


  @Test
  public void testGetColumns() {
    for (Column col : FileUtils.getColumnList("ZooSchema", "Zoo")) {
      System.out.println(col.getName());
    }
  }

}
