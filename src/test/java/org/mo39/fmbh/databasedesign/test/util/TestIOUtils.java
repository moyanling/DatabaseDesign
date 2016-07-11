package org.mo39.fmbh.databasedesign.test.util;

import org.junit.Test;
import org.mo39.fmbh.databasedesign.utils.IOUtils;

public class TestIOUtils {

  @Test(expected = IllegalArgumentException.class)
  public void testNullArgumentCheckFortblRef() {
    IOUtils.tblRef(null, "");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullArgumentCheckForndxRef() {
    IOUtils.ndxRef(null, null, "");
  }

}
