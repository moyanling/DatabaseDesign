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

  @Test
  public void testInsertToTableRegularExpression() {
    String testString = "INSERT  INTO  TABLE  table_name  VALUES  (value1,value2,value3,…)  ;";
    String regex = "^INSERT\\s+INTO\\s+TABLE\\s+(.*?)\\s+VALUES\\s+\\((.*?)\\)\\s*\\;$";
    Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    Matcher matcher = p.matcher(testString);
    Assert.assertTrue(matcher.matches());
    Assert.assertEquals("table_name", matcher.group(1));
    Assert.assertEquals("value1,value2,value3,…", matcher.group(2));
  }

  @Test
  public void testSelectWithWhereClauseRegularExpression() {
    String testString = "select class from table_name where id = 1;";
    String test = "select class from table_name ;";

    String regex = "^SELECT\\s+(.*?)\\s+FROM\\s+(.*?)(\\s*?\\;$|\\s+WHERE\\s+(.*?[>=<].*?)\\s*?\\;$)";
    Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    Matcher matcher = p.matcher(testString);
    Assert.assertTrue(matcher.matches());
    Assert.assertEquals("class", matcher.group(1));
    Assert.assertEquals("table_name", matcher.group(2));
    Assert.assertEquals("where id = 1;", matcher.group(3).trim());

    matcher = p.matcher(test);
    Assert.assertTrue(matcher.matches());
    Assert.assertEquals("class", matcher.group(1));
    Assert.assertEquals("table_name", matcher.group(2));
    Assert.assertEquals(";", matcher.group(3).trim());
  }









}


