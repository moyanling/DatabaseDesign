package org.mo39.fmbh.databasedesign.test.util;

import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mo39.fmbh.databasedesign.framework.DatabaseDesign;
import org.mo39.fmbh.databasedesign.utils.FileUtils;

import com.google.common.collect.Sets;

public class TestFileUtils {

  @Before
  public void before() {
    new DatabaseDesign();
  }

  @Test
  public void testGetSchemas() {
    // "schema_1", "schema_2", "schema_3"
    Set<String> schemas = Sets.newHashSet("INFORMATION_SCHEMA");
    Assert.assertEquals(schemas, FileUtils.getSchemaSet());
  }

  @Test
  public void testGetTables() {
    Set<String> tables = Sets.newHashSet("SCHEMATA", "TABLES", "COLUMNS");
    Assert.assertEquals(tables, FileUtils.getTableSet("INFORMATION_SCHEMA"));
  }

  // @Test
  // public void testValidate() {
  // Assert.assertTrue(FileUtils.validateSchemas());
  // for (String schema : FileUtils.getSchemaSet()) {
  // Assert.assertTrue(FileUtils.validateTables(schema));
  // }
  // }

  @Test
  public void testGetColumns() {
    // for (Column col : FileUtils.getColumns("Information_Schema", "tables")) {
    // System.out.println(col.getName());
    // }
  }

}
