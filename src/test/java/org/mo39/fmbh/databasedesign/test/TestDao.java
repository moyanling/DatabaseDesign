package org.mo39.fmbh.databasedesign.test;

import java.lang.reflect.InvocationTargetException;

import org.mo39.fmbh.databasedesign.dao.DatabaseDao;
import org.mo39.fmbh.databasedesign.dao.DatabaseDaoImpl;

public class TestDao {
  public static void main(String[] args) throws NoSuchMethodException, SecurityException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    DatabaseDao x = DatabaseDaoImpl.INSTANCE;
    DatabaseDao.class.getMethod("use").invoke(x);
  }
}
