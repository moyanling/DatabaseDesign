package org.mo39.fmbh.databasedesign.test.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mo39.fmbh.databasedesign.framework.DatabaseDesign;

import com.google.common.collect.Lists;

public class TestTblUtils {

  @Before
  public void before() {
    new DatabaseDesign();
  }

  @Test
  public void testObjectToString() {
    Object obj = new Integer(123);
    Assert.assertEquals(obj.toString(), "123");

    Object obj1 = 1234;
    Assert.assertEquals(obj1.toString(), "1234");
  }

  @Test
  public void testByteArrayOutputStream() throws IOException {
    ByteArrayOutputStream x = new ByteArrayOutputStream();
    x.write(new byte[] {3, 4, 5, 6, 7});
    x.reset();
    x.close();
  }

  @Test
  public void testAddNullToList() {
    List<Object> toRet = Lists.newArrayList();
    toRet.add(null);
    System.out.println(toRet);
  }

}
