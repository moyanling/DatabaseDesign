package org.mo39.fmbh.databasedesign.test.util;

import org.junit.Assert;
import org.junit.Test;

public class TestTblUtils {
  
  @Test
  public void testObjectToString() {
    Object obj = new Integer(123);
    Assert.assertEquals(obj.toString(), "123");
    
    Object obj1 = 1234;
    Assert.assertEquals(obj1.toString(), "1234");
  }

}
