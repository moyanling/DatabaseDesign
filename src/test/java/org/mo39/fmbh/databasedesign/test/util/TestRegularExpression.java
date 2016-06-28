package org.mo39.fmbh.databasedesign.test.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class TestRegularExpression {

  @Test
  public void testColumnRegx() {
    String arg1 = "column_name1    data_type(size) ";
    String arg2 = "column_1 int primary key";
    Pattern regx = Pattern.compile("^(.*?)\\s+(.*?)(\\s*?$|\\s+(.*?)\\s*?$)");
    Matcher m;
    
    m = regx.matcher(arg1);
    Assert.assertTrue(m.matches());
    Assert.assertEquals("column_name1", m.group(1).trim());
    Assert.assertEquals("data_type(size)", m.group(2).trim());
    Assert.assertEquals("", m.group(3).trim());
    
    m = regx.matcher(arg2);
    Assert.assertTrue(m.matches());
    Assert.assertEquals("column_1", m.group(1).trim());
    Assert.assertEquals("int", m.group(2).trim());
    Assert.assertEquals("primary key", m.group(3).trim());
  }
}
