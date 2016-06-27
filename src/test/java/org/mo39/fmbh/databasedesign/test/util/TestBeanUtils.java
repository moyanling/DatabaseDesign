package org.mo39.fmbh.databasedesign.test.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mo39.fmbh.databasedesign.framework.DatabaseDesign;
import org.mo39.fmbh.databasedesign.model.Column;
import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.test.model.TestEntity;
import org.mo39.fmbh.databasedesign.utils.BeanUtils;
import org.mo39.fmbh.databasedesign.utils.DataTypeParsingUtils;


public class TestBeanUtils {

  @Before
  public void before() {
    new DatabaseDesign();
  }

  @Test
  public void testConvert() throws Exception {
    TestEntity t1 = new TestEntity();
    BeanUtils.findMethod(TestEntity.class, "setId", "java.lang.Integer").invoke(t1,
        DataTypeParsingUtils.parseIntFromBytes(new byte[] {0, 0, 0, 1}));
    Assert.assertEquals(1, t1.getId());
  }

  @Test
  public void testBeanToString() throws DBExceptions {
    TestEntity t1 = new TestEntity();
    t1.setId(1);
    t1.setDescription("description");
    System.out.println(BeanUtils.beanToString(t1));
    System.out.println(BeanUtils.beanToString(Column.newColumnDefinition("SCHEMA_NAME VARCHAR(20)")));
  }

}
