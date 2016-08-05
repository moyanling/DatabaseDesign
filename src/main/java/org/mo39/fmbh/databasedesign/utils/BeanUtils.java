package org.mo39.fmbh.databasedesign.utils;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.text.WordUtils;
import org.mo39.fmbh.databasedesign.model.Column;
import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.DataType;

/**
 * Simple ORM without hbm.xml file.
 *
 * @author Jihan Chen
 *
 */
public abstract class BeanUtils {

  /**
   * Parse a byte array according to Column definitions to create a bean. If the first byte of the
   * record is not 1, it means this record is not active and will return null;
   *
   * @param beanClass
   * @param columns
   * @return
   */
  public static final Object parse(Class<?> beanClass, List<Column> columns, ByteBuffer record) {
    try {
      Object toRet = beanClass.newInstance();
      for (Column col : columns) {
        String methodName = "set" + WordUtils.capitalize(col.getName());
        Method m = findMethod(beanClass, methodName, col.getDataType().getJavaClass());
        m.invoke(toRet,
            DataType.class.getMethod(col.getDataType().getParseFromByteBuffer(), ByteBuffer.class)
                .invoke(null, record));
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
        m = beanClass.getMethod(method, converToPrimitiveType(classType));
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
   * <p>
   * use {@link ReflectionToStringBuilder.reflectionToString} instead
   *
   * @param t
   * @return
   */
  @Deprecated
  public static final <T> String beanToString(T t) {

    Pattern pattern = Pattern.compile("get(.*)");
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


  /**
   * Convert from class to corresponding primitive type.
   *
   * @param classType
   * @return
   */
  private static Class<?> converToPrimitiveType(String classType) {
    switch (classType) {
      case "java.lang.Integer":
        return Integer.TYPE;
      case "java.lang.Byte":
        return Byte.TYPE;
      case "java.lang.Short":
        return Short.TYPE;
      case "java.lang.Long":
        return Long.TYPE;
      case "java.lang.Double":
        return Double.TYPE;
      case "java.lang.Float":
        return Float.TYPE;
      default:
        DBExceptions.newError("Not a primitive type. Method not found.");
        return null;
    }
  }



}
