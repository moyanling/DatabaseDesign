package org.mo39.fmbh.databasedesign.test.util;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mo39.fmbh.databasedesign.utils.FileUtils;

import com.google.common.collect.Sets;

public class TestFileUtils {
  
  @Test
  public void testGetSchemas() {
    Set<String> schemas = Sets.newHashSet("schema_1","schema_2","schema_3");
    Assert.assertEquals(schemas, FileUtils.getSchemas());
  }
  
  @Test
  public void testGetTables() {
    Set<String> tables = Sets.newHashSet("table_1","table_2");
    Assert.assertEquals(tables, FileUtils.getTables("schema_1"));
  }

}
