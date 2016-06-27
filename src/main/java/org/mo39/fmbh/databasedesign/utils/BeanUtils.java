package org.mo39.fmbh.databasedesign.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.WordUtils;
import org.mo39.fmbh.databasedesign.model.Column;
import org.mo39.fmbh.databasedesign.model.DBExceptions;

/**
 * Simple ORM without hbm.xml file.
 *
 * @author Jihan Chen
 *
 */
public abstract class BeanUtils {

  /**
   * Parse Column List to create a bean.
   *
   * @param beanClass
   * @param columns
   * @return
   */
  public static final <T> T parse(Class<T> beanClass, List<Column> columns) {
    try {
      T toRet = beanClass.newInstance();
      for (Column col : columns) {
        String methodName = "set" + WordUtils.capitalize(col.getName());
        Method m = findMethod(beanClass, methodName, col.getDataType().getName());
        m.invoke(toRet, col.getValue());
      }
      return toRet;
    } catch (Exception e) {
      DBExceptions.newError(e);
    }
    return null;
  }

  /**
   * Find a method from a class.
   *
   * @param beanClass
   * @param method
   * @param classType
   * @return
   */
  public static final Method findMethod(Class<?> beanClass, String method, String classType) {
    Method m = null;
    try {
      m = beanClass.getMethod(method, Class.forName(classType));
    } catch (NoSuchMethodException e) {
      try {
        m = beanClass.getMethod(method, DataTypeParsingUtils.converToPrimitiveType(classType));
      } catch (Exception ex) {
        DBExceptions.newError(ex);
      }
    } catch (Exception ex) {
      DBExceptions.newError(ex);
    }
    return m;
  }

  /**
   * Return a String description of a bean.
   *
   * @param beanClass
   * @param t
   * @return
   */
  public static final <T> String beanToString(T t) {

    Pattern pattern = Pattern.compile("get([a-zA-Z]*?)");
    StringBuilder sb = new StringBuilder(t.getClass().getSimpleName() + ": {");
    List<Method> methods = Arrays.asList(t.getClass().getMethods());
    try {
      Matcher matcher;
      String methodName;
      for (Method getter : methods) {
        methodName = getter.getName();
        matcher = pattern.matcher(methodName);
        if (!methodName.equals("getClass") && matcher.matches()) {
          sb.append(WordUtils.uncapitalize(matcher.group(1)));
          sb.append("=");
          sb.append(getter.invoke(t) + ", ");
        }
      }
      return sb.substring(0, sb.length() - 2) + "} ";
    } catch (Exception e) {
      DBExceptions.newError(e);
    }
    return null;

  }



}
