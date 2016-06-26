package org.mo39.fmbh.databasedesign.test.util;

import static org.mo39.fmbh.databasedesign.framework.SystemProperties.COLUMNS;
import static org.mo39.fmbh.databasedesign.framework.SystemProperties.INFO_SCHEMA;
import static org.mo39.fmbh.databasedesign.framework.SystemProperties.TABLES;
import static org.mo39.fmbh.databasedesign.utils.FileUtils.tblRef;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.mo39.fmbh.databasedesign.framework.SystemProperties;
import org.mo39.fmbh.databasedesign.utils.TblUtils;

import com.google.common.base.Predicates;


public class TestTblUtils {
  private static File tables = tblRef(INFO_SCHEMA, TABLES);
  private static File columns = tblRef(INFO_SCHEMA, COLUMNS);

  @Test
  public void testDeleteLines() throws IOException {
    String pattern = "schema_1" + SystemProperties.DELIMITER + "table_1";
    Assert.assertTrue(TblUtils.deleteLines(tables, Predicates.containsPattern(pattern)));
    Assert.assertTrue(TblUtils.deleteLines(columns, Predicates.containsPattern(pattern)));
  }

}
