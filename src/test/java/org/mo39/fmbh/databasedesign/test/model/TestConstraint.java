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
    Constraint primary = Constraint.supports("primary key");
    assertEquals(PrimaryKey.class, primary.getClass());
    assertTrue(Constraint.class.isAssignableFrom(primary.getClass()));

  }

}
