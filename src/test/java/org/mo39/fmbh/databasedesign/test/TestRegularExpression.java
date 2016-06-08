package org.mo39.fmbh.databasedesign.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class TestRegularExpression {

  @Test
  public void testUse(){
    Pattern regx;
    Matcher matcher;

    Assert.isTrue("USE S;".matches("^USE.*?\\;$"));
    regx = Pattern.compile("^USE.*?\\;$", Pattern.CASE_INSENSITIVE);
    matcher = regx.matcher("use S;");
    Assert.isTrue(matcher.matches());

    regx = Pattern.compile("^EXIT(.*?)\\;$", Pattern.CASE_INSENSITIVE);
    matcher = regx.matcher("exit adjkls;");
    Assert.isTrue(matcher.matches());
    System.out.println(matcher.group(1));
  }

}
