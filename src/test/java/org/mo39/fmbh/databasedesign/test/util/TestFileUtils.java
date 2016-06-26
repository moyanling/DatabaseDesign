package org.mo39.fmbh.databasedesign.test.util;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mo39.fmbh.databasedesign.utils.FileUtils;

import com.google.common.collect.Sets;

public class TestFileUtils {

  @Test
  public void testGetSchemas() {
    // "schema_1", "schema_2", "schema_3"
    Set<String> schemas = Sets.newHashSet();
    Assert.assertEquals(schemas, FileUtils.getSchemas());
  }

  @Test
  public void testGetTables() {
    // "table_1", "table_2"
    Set<String> tables = Sets.newHashSet();
    Assert.assertEquals(tables, FileUtils.getTables("schema_1"));
  }

  @Test
  public void testValidate() {
    Assert.assertTrue(FileUtils.validateSchemas());
    for (String schema : FileUtils.getSchemas()) {
      Assert.assertTrue(FileUtils.validateTables(schema));
    }

  }

}
