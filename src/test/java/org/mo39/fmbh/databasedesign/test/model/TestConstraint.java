package org.mo39.fmbh.databasedesign.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mo39.fmbh.databasedesign.model.Constraint;
import org.mo39.fmbh.databasedesign.model.Constraint.Primary;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestConstraint {

  @SuppressWarnings({"unchecked", "resource"})
  @Before
  public void before() {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    Constraint.setConstraintList((List<Constraint>) ctx.getBean("supportedConstraintList"));
  }

  @Test
  public void testSubClass() {
    Constraint primary = Constraint.supports("primary key");
    assertEquals(Primary.class, primary.getClass());
    assertTrue(primary.getClass().isAssignableFrom(Constraint.class));

  }

}
