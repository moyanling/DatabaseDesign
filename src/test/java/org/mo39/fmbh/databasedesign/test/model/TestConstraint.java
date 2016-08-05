package org.mo39.fmbh.databasedesign.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mo39.fmbh.databasedesign.framework.DatabaseDesign;
import org.mo39.fmbh.databasedesign.model.Constraint;
import org.mo39.fmbh.databasedesign.model.Constraint.PrimaryKey;

public class TestConstraint {

  @Before
  public void before() {
    new DatabaseDesign();
  }

  @Test
  public void testSubClass() {
    Constraint primary = Constraint.valueOf("primary key");
    assertEquals(PrimaryKey.class, primary.getClass());
    assertTrue(Constraint.class.isAssignableFrom(primary.getClass()));
  }

  @Test
  public void testArrayIndex() {
    String[] x = new String[] {"1", "2", "3"};
    assertEquals("2", x[x.length - 2]);
    assertEquals("3", x[x.length - 1]);
  }

}
