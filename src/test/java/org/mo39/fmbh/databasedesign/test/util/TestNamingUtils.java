package org.mo39.fmbh.databasedesign.test.util;

import static org.mo39.fmbh.databasedesign.utils.FileUtils.ndxRef;
import static org.mo39.fmbh.databasedesign.utils.FileUtils.tblRef;

import org.junit.Test;

public class TestNamingUtils {

  @Test(expected = IllegalArgumentException.class)
  public void testNullArgumentCheckFortblRef() {
    tblRef(null, "");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullArgumentCheckForndxRef() {
    ndxRef(null, null, "");
  }

}
