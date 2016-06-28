package org.mo39.fmbh.databasedesign.test.framework;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Joiner;
public class TestInfoSchema {

  public static class X {
    public String toString() {
      return "X";
    }
  }

  @Test
  public void TestJoiner() {
    String str = Joiner.on(',').join("a", new X());
    Assert.assertEquals("a,X", str);
  }
  

}
