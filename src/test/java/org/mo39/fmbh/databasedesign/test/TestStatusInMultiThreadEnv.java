package org.mo39.fmbh.databasedesign.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.mo39.fmbh.databasedesign.framework.Status;


public class TestStatusInMultiThreadEnv {

  @Test
  public void testStatusInMultiThreadEnv() {
    // Thread t1
    new Thread(() -> {
      Status instance = Status.getInstance();
      instance.setCurrentSchema("t1 schema");
      assertEquals(instance.getCurrentSchema(), "t1 schema");
    }).start();
    // Thread t2
    new Thread(() -> {
      Status instance = Status.getInstance();
      instance.setCurrentSchema("t2 schema");
      assertEquals(instance.getCurrentSchema(), "t2 schema");
    }).start();
    // Main Thread
    assertNull(Status.getInstance().getCurrentSchema());
    assertNull(Status.getInstance().getCurrentCmd());
  }

}
