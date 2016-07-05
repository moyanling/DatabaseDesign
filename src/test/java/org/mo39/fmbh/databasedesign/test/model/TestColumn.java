package org.mo39.fmbh.databasedesign.test.model;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

public class TestColumn {

  public static class MockColumn implements Comparable<MockColumn> {
    private int order;

    public MockColumn(int order) {
      this.order = order;

    }

    @Override
    public int compareTo(MockColumn o) {
      return order - o.order;
    }
  }

  @Test
  public void testSortColumn() {
    MockColumn col1 = new MockColumn(0);
    MockColumn col2 = new MockColumn(1);
    ArrayList<MockColumn> cols1 = Lists.newArrayList(col1, col2);
    ArrayList<MockColumn> cols2 = Lists.newArrayList(col1, col2);
    Assert.assertTrue(cols1.get(0) == cols2.get(0));
    Assert.assertEquals(cols1, cols2);
    Collections.sort(cols2);
    Assert.assertEquals(cols1, cols2);
  }

}
