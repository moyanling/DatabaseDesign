package org.mo39.fmbh.databasedesign.test;

import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mo39.fmbh.databasedesign.dao.DatabaseDao;
import org.mo39.fmbh.databasedesign.dao.OperationAnnotation.SchemaOperation;
import org.mo39.fmbh.databasedesign.dao.OperationAnnotation.TableOperation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class TestAnnotation {

  @Test
  public void testAnnotation() throws NoSuchMethodException, SecurityException {
    Method m = DatabaseDao.class.getMethod("createTable");
    TableOperation t = m.getAnnotation(TableOperation.class);
    Assert.notNull(t);
    Assert.isTrue(t.requiresActiveSchema());
    SchemaOperation s = m.getAnnotation(SchemaOperation.class);
    Assert.isNull(s);
  }

}
