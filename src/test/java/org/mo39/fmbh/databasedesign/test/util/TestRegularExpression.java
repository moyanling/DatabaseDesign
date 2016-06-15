package org.mo39.fmbh.databasedesign.test.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class TestRegularExpression {

  @Test
  public void testColumnRegx() {
    String arg1 = "column_name1    data_type(size) ";
    String arg2 = "column_name1   data_type(size)  [primary key|not null] ";
    Pattern regx = Pattern.compile("^(.*?)\\s+(.*?)(\\s*?$|\\s+(.*?)\\s*?$)");
    match(regx.matcher(arg1), "");
    match(regx.matcher(arg2), "[primary key|not null]");

  }

  private static void match(Matcher m, String expected) {
    Assert.assertTrue(m.matches());
    Assert.assertEquals("column_name1", m.group(1).trim());
    Assert.assertEquals("data_type(size)", m.group(2).trim());
    Assert.assertEquals(expected, m.group(3).trim());
  }
}
