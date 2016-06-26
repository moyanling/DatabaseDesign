package org.mo39.fmbh.databasedesign.utils;

/**
 * This class helps to handle the convert between String and Bean class
 * 
 * @author Jihan Chen
 *
 */
public abstract class BeanUtils {

  public static final <T> T parse(String recordDescription, Class<T> T)
      throws InstantiationException, IllegalAccessException {
    return T.newInstance();

  }

}
