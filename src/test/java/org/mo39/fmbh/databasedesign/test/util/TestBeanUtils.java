package org.mo39.fmbh.databasedesign.test.util;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mo39.fmbh.databasedesign.framework.DatabaseDesign;
import org.mo39.fmbh.databasedesign.model.Column;
import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.DataType;
import org.mo39.fmbh.databasedesign.model.Row;
import org.mo39.fmbh.databasedesign.test.model.TestEntity;
import org.mo39.fmbh.databasedesign.utils.BeanUtils;


public class TestBeanUtils {

  public static Object get() {
    return 1;
  }

  @Before
  public void before() {
    new DatabaseDesign();
  }

  @Test
  public void testConvert() throws Exception {
    TestEntity t1 = new TestEntity();
    BeanUtils.findMethod(TestEntity.class, "setId", "java.lang.Integer").invoke(t1,
        DataType.parseIntFromByteBuffer(ByteBuffer.wrap(new byte[] {0, 0, 0, 1})));
    Assert.assertEquals(1, t1.getId());
  }

  @Test
  public void testBeanToString() throws DBExceptions {
    TestEntity t1 = new TestEntity();
    t1.setId(1);
    t1.setDescription("description");
    Assert.assertEquals("TestEntity: {id=1, description=description} ", BeanUtils.beanToString(t1));

    TestEntity t2 = new TestEntity();
    t2.setId(39);
    t2.setDescription("HelloWorld");
    Assert.assertEquals("TestEntity: {id=39, description=HelloWorld} ", BeanUtils.beanToString(t2));
  }

  @Test
  public void testParse() {
    Row row = Row.init(Column.newColumnDefinition("id int, description varchar(20)"));
    row.addRecord("39, Hello world");
    TestEntity t1 =
        BeanUtils.parse(TestEntity.class, row.getColumns(), ByteBuffer.wrap(row.getRecord()));
    Assert.assertEquals(39, t1.getId());
    Assert.assertEquals("Hello world", t1.getDescription());
  }

}
